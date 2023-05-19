package com.deven.nozdormu.timer.dto;

import lombok.Getter;
import org.apache.commons.lang3.EnumUtils;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public enum StatusEnums {
    /**
     *
     */
    BEEN_PERSISTENT(1, "persistence completion"),
    PUSH_SUCCESS(3, "push succeeded"),
    DELAY_PUSH_SUCCESS(-3, "indemnifying measure"),
    PURSH_FAILED(-9, "push failed");

    private Integer status;
    private String desc;

    StatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDesc(Integer status) {
        if (Objects.isNull(status)) {
            return "";
        }
        AtomicReference<String> desc = new AtomicReference<>("");
        EnumUtils.getEnumList(StatusEnums.class)
                .stream().filter(val -> val.getStatus().equals(status))
                .findFirst().ifPresent(val -> desc.set(val.getDesc()));
        return desc.get();
    }

}
