package com.deven.nozdormu.timer.dto;

import lombok.Getter;

@Getter
public enum StatueEnums {
    BEEN_PERSISTENT(1, "persistence completion"),
    IN_CACHE(2, "successfully put into cache"),
    PUSH_SUCCESS(3, "push succeeded"),
    DELAY_PUSH_SUCCESS(-3, "indemnifying measure"),
    PURSH_FAILED(-9, "push failed");

    private Integer statue;
    private String desc;

    StatueEnums(Integer statue, String desc) {
        this.statue = statue;
        this.desc = desc;
    }


}
