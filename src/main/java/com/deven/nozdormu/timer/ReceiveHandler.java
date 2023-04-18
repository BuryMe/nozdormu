package com.deven.nozdormu.timer;

import com.alibaba.fastjson.JSON;
import com.deven.nozdormu.timer.config.CommonConfig;
import com.deven.nozdormu.timer.dto.MsgCommand;
import com.deven.nozdormu.timer.dto.ReceiveMsg;
import com.deven.nozdormu.timer.dto.StatueEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年04月11日 5:26 PM
 */
@Service
@Slf4j
public class ReceiveHandler {

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private RedissonClient redissonClient;

    public void run(MsgCommand msgCommand) {
        if (!msgCommand.verify()) {
            log.error("parameter is illegal, msgCommand:{}", JSON.toJSON(msgCommand));
            return;
        }

        Long thisCycleEndTime = Long.valueOf(System.getProperty("thisCycleEndTime"));

        long currentTime = Instant.now().toEpochMilli();
        Long expectPushTime = msgCommand.getExpectPushTime();

        // whatever, The data is persisted first.
        ReceiveMsg receiveMsg = new ReceiveMsg(currentTime, msgCommand);
        receiveMsg.setStatue(StatueEnums.BEEN_PERSISTENT.getStatue());
        try {
            jdbcManager.insertReceiveMsg(receiveMsg);
        } catch (Exception e) {
            log.error("insertReceiveMsg fail, msgCommand:{}", JSON.toJSON(msgCommand), e);
            return;
        }

        if (expectPushTime < thisCycleEndTime) {
            int i = RandomUtils.nextInt(0, commonConfig.getQueueSize());
            RQueue<ReceiveMsg> queue = redissonClient.getQueue("delay_queue_" + i);
            RDelayedQueue<ReceiveMsg> delayedQueue = redissonClient.getDelayedQueue(queue);
            long delay = (receiveMsg.getExpectPushTime() - currentTime);
            if (delay < 0) {
                delay = 0;
            }
            delayedQueue.offer(receiveMsg, delay, TimeUnit.MILLISECONDS);
        }

    }
}


