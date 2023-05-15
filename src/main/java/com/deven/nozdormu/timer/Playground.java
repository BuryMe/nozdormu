package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.ReceiveMsg;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年05月15日 10:07 AM
 */
@Slf4j
@Service
public class Playground {

    public void demo() {
        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNameFormat("wheelTimer").get();
        HashedWheelTimer wheelTimer = new HashedWheelTimer(namedFactory, 100, TimeUnit.MILLISECONDS,
                1024, false);
        wheelTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {

            }
        }, 10, TimeUnit.MILLISECONDS);
    }

    @Autowired
    @Qualifier(value = "scheduledExecutor")
    private ScheduledThreadPoolExecutor scheduledExecutor;

    @Autowired
    JdbcManager jdbcManager;

    @Autowired
    MqProducer mqProducer;

    public void run(HashedWheelTimer wheelTimer) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            String start1 = SpringProperties.getProperty("start");
            String end1 = SpringProperties.getProperty("end");
            if (StringUtils.isEmpty(start1) || StringUtils.isEmpty(end1)) {
                return;
            }

            Long start = Long.valueOf(start1);
            Long end = Long.valueOf(end1);
            log.info("----- start:{},end:{} ------", paresTime(start), paresTime(end));
            List<ReceiveMsg> receiveMsgList = jdbcManager.selectPersistentMsgList(start, end);
            if (!CollectionUtils.isEmpty(receiveMsgList)) {
                for (ReceiveMsg receiveMsg : receiveMsgList) {
                    long l = receiveMsg.getExpectPushTime() - System.currentTimeMillis();
                    wheelTimer.newTimeout(timeout -> mqProducer.sendMessageAsync(receiveMsg), l, TimeUnit.MILLISECONDS);
                }
            }

            SpringProperties.setProperty("start", end.toString());
            SpringProperties.setProperty("end", String.valueOf(end + 60000));

        }, 0, 60, TimeUnit.SECONDS);
    }

    public String paresTime(Long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    public static void main(String[] args) throws IOException {
        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNameFormat("wheelTimer").get();
        HashedWheelTimer wheelTimer = new HashedWheelTimer(namedFactory, 100, TimeUnit.MILLISECONDS,
                600, false);
        // one min.


        wheelTimer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("1111");
            }
        }, 5, TimeUnit.SECONDS);

        wheelTimer.start();


    }

}
