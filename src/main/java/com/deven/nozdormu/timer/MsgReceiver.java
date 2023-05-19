package com.deven.nozdormu.timer;

//import com.alibaba.fastjson.JSON;
//import com.deven.nozdormu.timer.config.CommonConfig;
import com.deven.nozdormu.timer.dto.MsgCommand;
import com.deven.nozdormu.timer.dto.ReceiveMsg;
import com.deven.nozdormu.timer.dto.StatusEnums;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomUtils;
//import org.redisson.api.RDelayedQueue;
//import org.redisson.api.RQueue;
//import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年04月11日 5:26 PM
 */
@Service
@Slf4j
public class MsgReceiver {

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    @Qualifier("hashedWheelTimer")
    private HashedWheelTimer wheelTimer;

    @Autowired
    MqProducer mqProducer;

    public void run(MsgCommand msgCommand) {

        long currentTime = Instant.now().toEpochMilli();
        ReceiveMsg receiveMsg = new ReceiveMsg(currentTime, msgCommand);
        receiveMsg.setStatus(StatusEnums.BEEN_PERSISTENT.getStatus());
        jdbcManager.insertReceiveMsg(receiveMsg);

        Long expectPushTime = msgCommand.getExpectPushTime();
        if (expectPushTime <= currentTime) {
            mqProducer.sendMessageAsync(receiveMsg);
            return;
        }

        String nextStart = SpringProperties.getProperty("start");
        Long nextStartTime = Long.valueOf(nextStart);
        if (expectPushTime >= nextStartTime) {
            //
        } else {
            long l = expectPushTime - Instant.now().toEpochMilli();
            wheelTimer.newTimeout(timeout -> mqProducer.sendMessageAsync(receiveMsg), l, TimeUnit.MILLISECONDS);
        }

    }
}


