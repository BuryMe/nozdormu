package com.deven.nozdormu.timer;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.netty.util.HashedWheelTimer;
//import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.SpringProperties;
import org.springframework.stereotype.Component;
//import sun.lwawt.macosx.CSystemTray;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年04月11日 5:45 PM
 */
@Slf4j
@Component
public class StartUpApp implements ApplicationRunner {

    @Autowired
    private MsgScheduler msgScheduler;

    @Override
    public void run(ApplicationArguments args) {
        long start = System.currentTimeMillis();
        long end = start + 60000;
        SpringProperties.setProperty("start", String.valueOf(start));
        SpringProperties.setProperty("end", String.valueOf(end));

        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNamePrefix("wheelTimer").build();
        HashedWheelTimer wheelTimer = new HashedWheelTimer(namedFactory, 100, TimeUnit.MILLISECONDS,
                600, false);

        msgScheduler.run(wheelTimer);
    }


}
