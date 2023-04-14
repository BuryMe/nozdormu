package com.deven.nozdormu.timer.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author seven up
 * @date 2023年04月11日 2:50 PM
 */
@Data
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String nodes;

    @Value("${spring.redis.password}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        String[] cluster = nodes.split(",");
        String addresses = Arrays.stream(cluster)
                .map(c -> "redis://" + c)
                .collect(Collectors.joining(","));
        Config config = new Config();
        config.useClusterServers().addNodeAddress(addresses.split(","))
                .setReadMode(ReadMode.MASTER)
                .setSubscriptionMode(SubscriptionMode.MASTER)
                .setMasterConnectionPoolSize(10)
                .setMasterConnectionMinimumIdleSize(1)
                .setPingConnectionInterval(1000)
                .setRetryAttempts(1)
                .setRetryInterval(1000)
                .setCheckSlotsCoverage(false)
                .setPassword(password)
        ;
        return Redisson.create(config);
    }
}
