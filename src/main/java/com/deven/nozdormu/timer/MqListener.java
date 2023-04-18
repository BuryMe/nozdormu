package com.deven.nozdormu.timer;

import com.alibaba.fastjson.JSON;
import com.deven.nozdormu.timer.dto.MsgCommand;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author seven up
 * @date 2023年04月10日 3:11 PM
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${rocketmq.consumer.topics}",
        consumerGroup = "${rocketmq.consumer.group}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING
)
public class MqListener implements RocketMQListener<String> {

    @Autowired
    private ReceiveHandler receiveService;

    @Override
    public void onMessage(String msgCommand) {
        try {
            MsgCommand command = JSON.parseObject(msgCommand, MsgCommand.class);
            receiveService.run(command);
        } catch (Exception e) {
            log.error("parameter anomaly msgCommand:{}", msgCommand);
        }

    }

}
