package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.config.CommonConfig;
import com.deven.nozdormu.timer.dto.ReceiveMsg;
import com.deven.nozdormu.timer.dto.StatueEnums;
import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author seven up
 * @date 2023年04月10日 2:33 PM
 */
@Slf4j
@Component
public class ScheduleService {

    private static final Long PERIOD = 60 * 1000 * 1000L;

    private static final Long PERIOD_MILLI = 60 * 1000L;

    private final static String SELECT_LOCK = "select_msg_lock";

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    @Qualifier(value = "scheduledExecutor")
    private ScheduledThreadPoolExecutor scheduledExecutor;

    @Autowired
    @Qualifier(value = "pushExecutor")
    private ThreadPoolTaskExecutor pushExecutor;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    RedissonClient redissonClient;

    private static final Map<Integer, RDelayedQueue<ReceiveMsg>> QUEUE_MAP = new HashMap<>(16);
    private static final Map<Integer, ScheduledThreadPoolExecutor> EXECUTOR_MAP = new HashMap<>(16);


    public void run() {
        for (int i = 0; i < commonConfig.getQueueSize(); i++) {
            ThreadFactory namedFactory = new ThreadFactoryBuilder().setNameFormat("listener-pool-" + i).get();
            ScheduledThreadPoolExecutor threadPoolTaskExecutor = new ScheduledThreadPoolExecutor(1, namedFactory);
            threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
            EXECUTOR_MAP.put(i, threadPoolTaskExecutor);

            RQueue<ReceiveMsg> queue = redissonClient.getQueue("delay_queue_" + i);
            RDelayedQueue<ReceiveMsg> delayedQueue = redissonClient.getDelayedQueue(queue);
            QUEUE_MAP.put(i, delayedQueue);
        }

        listener(commonConfig.getQueueSize());
        scanner();
    }

    private void listener(Integer queueSize) {

        for (int i = 0; i < queueSize; i++) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = EXECUTOR_MAP.get(i);
            RBlockingQueue<ReceiveMsg> blockingFairQueue = redissonClient.getBlockingQueue("delay_queue_" + i);
            redissonClient.getDelayedQueue(blockingFairQueue);

            int finalI = i;
            scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
                try {
                    log.info("current index:{}", finalI);
                    ReceiveMsg receiveMsg = blockingFairQueue.take();
                    pushExecutor.execute(() -> mqProducer.sendMessageAsync(receiveMsg));
                } catch (Exception e) {
                    log.error("task error", e);
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

    }

    private void scanner() {
        AtomicLong lineTime = new AtomicLong(0L);
        scheduledExecutor.scheduleAtFixedRate(() -> {

            long startTime;
            if (lineTime.get() == 0) {
                startTime = Instant.now().toEpochMilli();
            } else {
                startTime = lineTime.get();
            }
            long endTime = startTime + PERIOD_MILLI;
            lineTime.set(endTime);
            System.setProperty("thisCycleEndTime", String.valueOf(endTime));
            log.info("scanning time range. thisCycleEndTime:{},start:{},end:{}", endTime, startTime, endTime);

            // Distributed locking data
            List<ReceiveMsg> selectMsgList;
            RLock lock = redissonClient.getLock(SELECT_LOCK);
            if (!lock.tryLock()) {
                return;
            }
            try {
                lock.lock();
                selectMsgList = jdbcManager.selectPersistentMsgList(startTime, endTime);
                List<Long> idList = selectMsgList.stream().map(ReceiveMsg::getId).collect(Collectors.toList());
                jdbcManager.updateMsgStatueByIds(idList, StatueEnums.IN_CACHE);
            } catch (Exception e) {
                log.error("select msg error", e);
                return;
            } finally {
                lock.unlock();
            }

            selectMsgList.forEach(val -> {
                Long id = val.getId();
                int index = (int) (id % commonConfig.getQueueSize());
                RDelayedQueue<ReceiveMsg> delayedQueue = QUEUE_MAP.get(index);
                delayedQueue.offer(val, val.getExpectPushTime() - Instant.now().toEpochMilli(), TimeUnit.MILLISECONDS);
            });

        }, 0, PERIOD, TimeUnit.MICROSECONDS);
    }

}
