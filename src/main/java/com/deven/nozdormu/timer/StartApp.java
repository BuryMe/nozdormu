package com.deven.nozdormu.timer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author seven up
 * @date 2023年04月11日 5:45 PM
 */
@Slf4j
@Component
public class StartApp implements CommandLineRunner {

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public void run(String... args) {

        scheduleService.run();

    }


}
