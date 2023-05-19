package com.deven.nozdormu.timer.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author seven up
 * @date 2023年05月18日 11:13 AM
 */
@Data
public class PageVO {

    private Long id;
    private String uniqueKey;
    private String pushBody;
    private String pushTopic;
    private String pushTag;
    private String expectPushTime;
    private String receiveTime;
    private String createTime;
    private Integer status;
    private String resp;
    private String realPushTime;


}
