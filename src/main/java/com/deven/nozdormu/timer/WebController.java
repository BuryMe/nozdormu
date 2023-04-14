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

    public static void main(String[] args) {
        String s = "100091002\n" +
                "100091001\n" +
                "100028804\n" +
                "100028803\n" +
                "100028802\n" +
                "100028801\n" +
                "SG00001550\n" +
                "SG00001549\n" +
                "SG00001548\n" +
                "SG00001542\n" +
                "SG00001541\n" +
                "SG00001538\n" +
                "SG00001537\n" +
                "SG00001536\n" +
                "SG00001535\n" +
                "SG00001534\n" +
                "SG00001528\n" +
                "SG00001527\n" +
                "SG00001526\n" +
                "SG00001525\n" +
                "SG00001524\n" +
                "SG00001523\n" +
                "SG00001522\n" +
                "SG00001521\n" +
                "SG00001519\n" +
                "SG00001518\n" +
                "SG00001517\n" +
                "SG00001516\n" +
                "SG00001513\n" +
                "SG00001512\n" +
                "SG00001511\n" +
                "SG00001510\n" +
                "SG00001508\n" +
                "SG00001507\n" +
                "SG00001506\n" +
                "SG00001505\n" +
                "SG00001504\n" +
                "SG00001503\n" +
                "SG00001502\n" +
                "SG00016601\n" +
                "SG00014401\n" +
                "SG00001501";
        String ss = StringUtils.join(s.split("\n"),",");
        System.out.println(ss);

    }
}
