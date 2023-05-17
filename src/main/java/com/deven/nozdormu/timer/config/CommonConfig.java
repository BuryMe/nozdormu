package com.deven.nozdormu.timer.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.netty.util.HashedWheelTimer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author seven up
 * @date 2023年04月11日 3:21 PM
 */
@Configuration
public class CommonConfig {


    @Value("${mock.push}")
    private String mockPush;


    public Integer getMockPush() {
        return Integer.parseInt(mockPush);
    }

    @Bean("hashedWheelTimer")
    public HashedWheelTimer wheelTimer() {
        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNamePrefix("receiver-").build();
        return new HashedWheelTimer(namedFactory, 100, TimeUnit.MILLISECONDS,
                600, false);
    }

    @Bean("scheduledExecutor")
    public ScheduledThreadPoolExecutor scheduledExecutor() {
        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNamePrefix("scan-pool-0").build();
        ScheduledThreadPoolExecutor threadPoolTaskExecutor = new ScheduledThreadPoolExecutor(1, namedFactory);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return threadPoolTaskExecutor;
    }


    // ------------------------

    @Bean("mockExecutor")
    public ThreadPoolTaskExecutor mockExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(32);
        threadPoolTaskExecutor.setKeepAliveSeconds(10);
        threadPoolTaskExecutor.setQueueCapacity(1024);
        threadPoolTaskExecutor.setThreadNamePrefix("test-pool");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
