package com.deven.nozdormu.timer.dto;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author seven up
 * @date 2023年04月11日 11:42 AM
 */
@Data
public class MsgCommand {

    private String uniqueKey;

    private String pushBody;

    private String pushTopic;

    private String pushTag;

    /**
     * Arbitrary extension time
     */
    private Long expectPushTime;


    public Boolean verify(){
        boolean can = StringUtils.hasText(uniqueKey)
                && StringUtils.hasText(pushBody)
                && StringUtils.hasText(pushTopic)
                && StringUtils.hasText(pushTag);
        return can;
    }

}
