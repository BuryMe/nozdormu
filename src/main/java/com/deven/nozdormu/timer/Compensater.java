package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.ReceiveMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author seven up
 * @date 2023年05月17日 5:17 PM
 */
@Slf4j
@Service
public class Compensater {

    @Autowired
    private JdbcManager jdbcManager;

    @Autowired
    private MqProducer mqProducer;

    @Scheduled(cron = "0/10 * * * * ?")
    public void run() {
        List<ReceiveMsg> receiveMsgList = jdbcManager.selectPersistentMsgList(0L, Instant.now().toEpochMilli());
        if (CollectionUtils.isEmpty(receiveMsgList)) {
            return;
        }

        for (ReceiveMsg receiveMsg : receiveMsgList) {
            mqProducer.sendMessageAsync(receiveMsg);
        }
    }

}
