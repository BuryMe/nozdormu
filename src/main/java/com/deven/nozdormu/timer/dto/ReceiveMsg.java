package com.deven.nozdormu.timer.dto;

import lombok.Data;

/**
 * @author seven up
 * @date 2023年04月10日 2:24 PM
 */
@Data
public class ReceiveMsg {

    private String uniqueKey;

    private String pushBody;

    private String pushTopic;

    private String pushTag;

    /**
     * Arbitrary extension time
     */
    private Long expectPushTime;

    private Long receiveTime;

    private Long id;
    private Long createTime;
    private Integer status;
    private String resp;
    private Long realPushTime;

    public ReceiveMsg(Long receiveTime, MsgCommand command) {
        this.receiveTime = receiveTime;
        this.uniqueKey = command.getUniqueKey();
        this.pushBody = command.getPushBody();
        this.pushTopic = command.getPushTopic();
        this.pushTag = command.getPushTag();
        this.expectPushTime = command.getExpectPushTime();
        this.status = StatusEnums.BEEN_PERSISTENT.getStatus();
    }

    public ReceiveMsg(){

    }


}
