package com.deven.nozdormu.timer.dto;

import lombok.Data;

/**
 * @author seven up
 * @date 2023年05月18日 11:18 AM
 */
@Data
public class PageCmd {

    private Integer page = 10;
    private Integer size = 1;

    private String uniqueKey;
    private Integer status;



}
