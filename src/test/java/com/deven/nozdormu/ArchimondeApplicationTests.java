package com.deven.nozdormu;

import com.deven.nozdormu.timer.JdbcManager;
import com.deven.nozdormu.timer.MsgReceiver;
import com.deven.nozdormu.timer.dto.MsgCommand;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Random;

@SpringBootTest
class ArchimondeApplicationTests {

    @Test
    void contextLoads() {
    }


    @Resource
    JdbcManager receiveMsgDao;

    @Resource
    private MsgReceiver receiveService;

    @Test
    public void startTest() {
        for (int i = 0; i < 200; i++) {
            long currentTimeMillis = System.currentTimeMillis();
            MsgCommand msgCommand = new MsgCommand();
            msgCommand.setUniqueKey(String.valueOf(System.currentTimeMillis()));
            msgCommand.setPushBody(String.valueOf(i));
            msgCommand.setPushTopic(String.valueOf(i));
            msgCommand.setPushTag(String.valueOf(1));
            Random random = new Random();
            int randomNumber = random.nextInt(6001);
            msgCommand.setExpectPushTime(currentTimeMillis + randomNumber);

            receiveService.run(msgCommand);
        }
    }

    @Test
    public void testDao() {
//        ReceiveMsg msg = new ReceiveMsg();
//        msg.setUniqueKey("1");
//        msg.setReceiveTime(System.currentTimeMillis());
//        msg.setPushBody("11");
//        msg.setPushTopic("1");
//        msg.setPushTag("1");
//        msg.setDelayConsumingTime(System.currentTimeMillis());
//        receiveMsgDao.insert(msg);
    }

}
