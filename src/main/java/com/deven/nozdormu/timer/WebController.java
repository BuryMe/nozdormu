package com.deven.nozdormu.timer;

import com.alibaba.fastjson.JSON;
import com.deven.nozdormu.timer.dto.MsgCommand;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    private ReceiveHandler receiveService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @GetMapping("/testPush")
    public void testPush() {
        for (int i = 0; i < 10; i++) {
            MsgCommand command = new MsgCommand();
            long currentTimeMillis = System.currentTimeMillis();
            command.setUniqueKey(String.valueOf(currentTimeMillis));
            command.setPushBody("1");
            command.setPushTopic("1");
            command.setPushTag("1");
            Random random = new Random();
            long l = random.nextInt(60000);
            command.setExpectPushTime(currentTimeMillis + l);

            try {
                mockPush(command);
            } catch (MQClientException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void mockPush(MsgCommand command) throws MQClientException {
        // 实例化生产者对象
        DefaultMQProducer producer = new DefaultMQProducer("producer_group");
        // 设置NameServer地址，多个地址用分号隔开
        producer.setNamesrvAddr("192.168.4.34:9876");
        // 启动生产者
        producer.start();
        try {
            // 创建消息对象，指定Topic、Tag和消息内容
            Message message = new Message("mpzdormu_topic", "test_tag", JSON.toJSONString(command).getBytes());
            // 指定消息队列
//            MessageQueue messageQueue = new MessageQueue("mpzdormu_topic", "test_tag", 0);
            // 发送消息
            producer.send(message);
            System.out.println("消息发送成功");
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
