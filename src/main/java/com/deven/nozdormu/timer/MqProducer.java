package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.config.CommonConfig;
import com.deven.nozdormu.timer.dto.ReceiveMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author seven up
 * @date 2023年04月11日 11:28 AM
 */
@Component
@Slf4j
public class MqProducer {

    @Autowired
    private RocketMQTemplate mqTemplate;

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    CommonConfig commonConfig;

    public void sendMessageAsync(ReceiveMsg receiveMsg) {
        if (1 == commonConfig.getMockPush()) {
            mockSendMessageAsync(receiveMsg);
            return;
        }
        String destination = receiveMsg.getPushTopic() + ":" + receiveMsg.getPushTag();
        Message<String> message = MessageBuilder.withPayload(receiveMsg.getPushBody())
                .setHeader(MessageConst.PROPERTY_KEYS, receiveMsg.getUniqueKey())
                .build();
        long currentTime = Instant.now().toEpochMilli();
        mqTemplate.asyncSend(destination, message, new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                jdbcManager.updateMsgSetSuccess(currentTime, receiveMsg.getId(), receiveMsg.getUniqueKey(), sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                jdbcManager.updateMsgSetFail(currentTime, receiveMsg.getId(), receiveMsg.getUniqueKey(), e.getMessage());
            }
        });
    }

    // ---------------------------------------

    @Autowired
    @Qualifier("mockExecutor")
    private ThreadPoolTaskExecutor mockExecutor;

    private void mockSendMessageAsync(ReceiveMsg receiveMsg) {
        mockExecutor.execute(() -> {
            Long expectPushTime = receiveMsg.getExpectPushTime();
            long currentTime = Instant.now().toEpochMilli();

            long l = currentTime - expectPushTime;
            log.info("sendTime - expectPushTime = {}", l);

            jdbcManager.updateMsgSetSuccess(currentTime, receiveMsg.getId(), receiveMsg.getUniqueKey(), "1111");
        });
    }

}
