package com.deven.nozdormu.timer;

import com.alibaba.fastjson.JSON;
import com.deven.nozdormu.timer.dto.MsgCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
    private MsgReceiver receiveService;

    @Override
    public void onMessage(String msgCommand) {
        try {
            MsgCommand command = JSON.parseObject(msgCommand, MsgCommand.class);
            Assert.hasText(command.getUniqueKey());
            Assert.hasText(command.getPushBody());
            Assert.hasText(command.getPushTopic());
            Assert.hasText(command.getPushTag());
            receiveService.run(command);
        } catch (Exception e) {
            log.error("parameter anomaly msgCommand:{}", msgCommand, e);
        }

    }

}
