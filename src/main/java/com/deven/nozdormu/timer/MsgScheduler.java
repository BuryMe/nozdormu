package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.ReceiveMsg;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年05月15日 10:07 AM
 */
@Slf4j
@Service
public class MsgScheduler {


    @Autowired
    @Qualifier(value = "scheduledExecutor")
    private ScheduledThreadPoolExecutor scheduledExecutor;

    @Autowired
    JdbcManager jdbcManager;

    @Autowired
    MqProducer mqProducer;

    public void run(HashedWheelTimer wheelTimer) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            String start1 = SpringProperties.getProperty("start");
            String end1 = SpringProperties.getProperty("end");
            assert start1 != null;
            Long start = Long.valueOf(start1);
            assert end1 != null;
            Long end = Long.valueOf(end1);
            SpringProperties.setProperty("start", end.toString());
            SpringProperties.setProperty("end", String.valueOf(end + 10000));

            log.info("----- schedule start:{},end:{}  ------",
                    DateUtils.parseTime(start), DateUtils.parseTime(end));

            List<ReceiveMsg> receiveMsgList;
            try {
                receiveMsgList = jdbcManager.selectPersistentMsgList(start, end);
            } catch (Exception e) {
                log.error("selectPersistentMsgList error", e);
                receiveMsgList = new ArrayList<>();
            }
            if (!CollectionUtils.isEmpty(receiveMsgList)) {
                for (ReceiveMsg receiveMsg : receiveMsgList) {
                    long l = receiveMsg.getExpectPushTime() - System.currentTimeMillis();
                    wheelTimer.newTimeout(timeout -> mqProducer.sendMessageAsync(receiveMsg), l, TimeUnit.MILLISECONDS);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }


}
