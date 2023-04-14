package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.ReceiveMsg;
import com.deven.nozdormu.timer.dto.StatueEnums;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author seven up
 * @date 2023年04月10日 2:41 PM
 */
@Repository
public class JdbcManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertReceiveMsg(ReceiveMsg receiveMsg) {
        this.jdbcTemplate.update("insert into receive_msg" +
                        " (unique_key,receive_time,push_body,push_topic,push_tag,expect_push_time,statue)" +
                        " value (?,?,?,?,?,?,?)",
                receiveMsg.getUniqueKey(), receiveMsg.getReceiveTime(), receiveMsg.getPushBody(),
                receiveMsg.getPushTopic(), receiveMsg.getPushTag(), receiveMsg.getExpectPushTime(),
                receiveMsg.getStatue());
    }

    public List<ReceiveMsg> selectPersistentMsgList(Long start, Long end) {
        return this.jdbcTemplate.queryForList("select *\n" +
                "from receive_msg\n" +
                "where expect_push_time between ? and ?\n" +
                "and statue = 1;", ReceiveMsg.class, start, end);
    }

    public void updateMsgStatueByIds(List<Long> ids, StatueEnums statueEnums) {
        String join = StringUtils.join(ids, ",");
        this.jdbcTemplate.update("update receive_msg set statue = ? where id in (?)"
                , statueEnums.getStatue(), join);
    }

    public void updateMsgSetSuccess(Long currentTime, Long id, String msgKey, String respId) {
        this.jdbcTemplate.update("update receive_msg set statue = 3,real_push_time = ? ,resp = ? where id = ? or unique_key = ?",
                currentTime, respId, id, msgKey);
    }

    public void updateMsgSetFail(Long currentTime, Long id, String msgKey, String error) {
        this.jdbcTemplate.update("update receive_msg set statue = -1,real_push_time = ? ,resp = ? where id = ? or unique_key = ?",
                currentTime, error, id, msgKey);
    }
}
