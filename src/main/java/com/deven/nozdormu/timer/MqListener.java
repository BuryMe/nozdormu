package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.MsgCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author seven up
 * @date 2023年04月10日 3:11 PM
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${receiveMq.consumer.topic}", consumerGroup = "${receiveMq.consumer.group}")
public class MqListener implements RocketMQListener<MsgCommand> {

    @Autowired
    private ReceiveService receiveService;

    @Override
    public void onMessage(MsgCommand msgCommand) {
        receiveService.run(msgCommand);
    }

}
