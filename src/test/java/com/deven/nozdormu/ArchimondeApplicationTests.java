package com.deven.nozdormu;

import cn.hutool.json.JSONUtil;
import com.deven.nozdormu.timer.JdbcManager;
import com.deven.nozdormu.timer.MsgReceiver;
import com.deven.nozdormu.timer.WebController;
import com.deven.nozdormu.timer.dto.MsgCommand;
import com.deven.nozdormu.timer.dto.PageCmd;
import com.deven.nozdormu.timer.dto.PageVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
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

    @Autowired
    private WebController webController;

    @Test
    public void testDao() {
//        PageCmd cmd = new PageCmd();
//        cmd.setUniqueKey("5809");
//        List<PageVO> page = webController.page(cmd);
//        System.out.println(JSONUtil.toJsonStr(page));
    }

}
