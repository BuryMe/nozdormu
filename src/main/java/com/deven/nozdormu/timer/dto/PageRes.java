package com.deven.nozdormu.timer.dto;

import lombok.Data;

import java.util.List;

/**
 * @author seven up
 * @date 2023年05月18日 6:11 PM
 */
@Data
public class PageRes {
    private Long count;
    private List<PageVO> list;
}
