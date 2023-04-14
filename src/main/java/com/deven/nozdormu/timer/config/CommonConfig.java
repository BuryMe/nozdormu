package com.deven.nozdormu.timer.config;

import jodd.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author seven up
 * @date 2023年04月11日 3:21 PM
 */
@Configuration
public class CommonConfig {

    @Value("${queue.size}")
    private String queueSize;

    @Value("${mock.push}")
    private String mockPush;

    public Integer getQueueSize() {
        return Integer.parseInt(queueSize);
    }

    public Integer getMockPush() {
        return Integer.parseInt(mockPush);
    }

    @Bean("scheduledExecutor")
    public ScheduledThreadPoolExecutor scheduledExecutor() {
        ThreadFactory namedFactory = new ThreadFactoryBuilder().setNameFormat("scan-pool-0").get();
        ScheduledThreadPoolExecutor threadPoolTaskExecutor = new ScheduledThreadPoolExecutor(1,namedFactory);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return threadPoolTaskExecutor;
    }



    @Bean("pushExecutor")
    public ThreadPoolTaskExecutor pushExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(32);
        threadPoolTaskExecutor.setKeepAliveSeconds(10);
        threadPoolTaskExecutor.setQueueCapacity(1024);
        threadPoolTaskExecutor.setThreadNamePrefix("push-pool");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(60);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    private final static Integer SLOTS = 60;
    private final static Integer SLOT_INTERVAL = 1000;

//    @Bean("timeWheel")
//    public TimeWheel timeWheel(){
//        return new TimeWheel(SLOTS, SLOT_INTERVAL);
//    }


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
