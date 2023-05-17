package com.deven.nozdormu.timer;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.deven.nozdormu.timer.dto.MsgCommand;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

/**
 * @author seven up
 * @date 2023年04月12日 11:30 AM
 */
@RestController
@RequestMapping("/web")
public class ZPlayground {

    @Autowired
    private MsgReceiver receiveService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    public static void main(String[] args) throws Exception {
        mockPush();
    }


    private static void mockPush() throws MQClientException {
        // 实例化生产者对象
        DefaultMQProducer producer = new DefaultMQProducer("producer_group");
        // 设置NameServer地址，多个地址用分号隔开
        producer.setNamesrvAddr("124.220.184.17:9876");
        // 启动生产者
        producer.start();
        try {
            ConcurrencyTester tester = ThreadUtil.concurrencyTest(50, () -> {
                long l1 = Instant.now().toEpochMilli();
                long delay = RandomUtil.randomLong(0, 60000);
                MsgCommand command = new MsgCommand();
                command.setUniqueKey(String.valueOf(delay));
                command.setPushBody("1");
                command.setPushTopic("1");
                command.setPushTag("1");
                Random random = new Random();
                long l = random.nextInt(61000);
                command.setExpectPushTime(l1 + delay);
                try {
                    Message message = new Message("mpzdormu_topic", "test_tag", JSON.toJSONString(command).getBytes());
                    producer.send(message);
                    System.out.println("消息发送成功");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            tester.getInterval();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭生产者
            producer.shutdown();
        }
    }

    @GetMapping("/testRunning")
    public String test() {
        return "server is running!";
    }

    @PostMapping("/add")
    public Boolean add() {
        return true;
    }


}
