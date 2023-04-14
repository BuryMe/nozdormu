package com.deven.nozdormu.timer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author seven up
 * @date 2023年04月11日 9:45 AM
 */
@Data
@Configuration
public class MqConfig {

    @Value("${receiveMq.consumer.group}")
    private String receiveGroup;

    @Value("${receiveMq.consumer.topic}")
    private String receiveTopic;

}
