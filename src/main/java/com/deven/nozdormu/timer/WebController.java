package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.MsgCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author seven up
 * @date 2023年04月12日 11:30 AM
 */
@RestController
@RequestMapping("/web")
public class WebController {

    @Autowired
    private ReceiveService receiveService;

    @GetMapping("/test")
    public void test(){
        Random random = new Random();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            MsgCommand msgCommand = new MsgCommand();
            msgCommand.setUniqueKey(String.valueOf(System.currentTimeMillis()));
            msgCommand.setPushBody(String.valueOf(i));
            msgCommand.setPushTopic(String.valueOf(i));
            msgCommand.setPushTag(String.valueOf(1));
            int randomNumber = random.nextInt(1001);
            msgCommand.setExpectPushTime(currentTimeMillis + randomNumber);

            receiveService.run(msgCommand);
        }
    }


}
