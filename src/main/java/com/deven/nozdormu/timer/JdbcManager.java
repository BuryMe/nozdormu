package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
                        " (unique_key,receive_time,push_body,push_topic,push_tag,expect_push_time,status)" +
                        " value (?,?,?,?,?,?,?)",
                receiveMsg.getUniqueKey(), receiveMsg.getReceiveTime(), receiveMsg.getPushBody(),
                receiveMsg.getPushTopic(), receiveMsg.getPushTag(), receiveMsg.getExpectPushTime(),
                receiveMsg.getStatus());
    }

    public List<ReceiveMsg> selectPersistentMsgList(Long start, Long end) {
        String sql = "select *\n" +
                "from receive_msg\n" +
                "where expect_push_time between " + start + " and " + end + "\n" +
                "and statue = 1;";
        List<Map<String, Object>> maps = this.jdbcTemplate.queryForList(sql);
        return get(maps);
    }

    public void updateMsgStatueByIds(List<Long> ids, StatusEnums statusEnums) {
        String join = StringUtils.join(ids, ",");
        this.jdbcTemplate.update("update receive_msg set status = ? where id in (?)"
                , statusEnums.getStatus(), join);
    }

    public void updateMsgSetSuccess(Long currentTime, Long id, String msgKey, String respId) {
        this.jdbcTemplate.update("update receive_msg set status = 3,real_push_time = ? ,resp = ? where id = ? or unique_key = ?",
                currentTime, respId, id, msgKey);
    }

    public void updateMsgSetFail(Long currentTime, Long id, String msgKey, String error) {
        this.jdbcTemplate.update("update receive_msg set status = -1,real_push_time = ? ,resp = ? where id = ? or unique_key = ?",
                currentTime, error, id, msgKey);
    }

    public PageRes page(PageCmd cmd) {
        PageRes res = new PageRes();

        List<String> whereList = new LinkedList<>();
        if (StringUtils.isNotBlank(cmd.getUniqueKey())) {
            whereList.add("unique_key = '" + cmd.getUniqueKey() + "'");
        }
        if (Objects.nonNull(cmd.getStatus())) {
            whereList.add("status = " + cmd.getStatus());
        }

        int setPage = Math.max((cmd.getPage() - 1), 0);
        int offSet = setPage * cmd.getSize();

        if (CollectionUtils.isEmpty(whereList)) {
            List<Map<String, Object>> maps = this.jdbcTemplate.queryForList(
                    "select * from receive_msg order by id desc limit " + offSet + "," + cmd.getSize() + ";");
            res.setList(getPageVo(maps));
            List<Map<String, Object>> maps1 = this.jdbcTemplate.queryForList("select count(1) as cc from receive_msg");
            Object count = maps1.get(0).get("cc");
            res.setCount((Long) count);
            return res;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from receive_msg where ");
        String columStr = String.join("and ", whereList);
        sqlBuilder.append(columStr).append(" order by id desc ");
        sqlBuilder.append(" limit ").append(offSet).append(",").append(cmd.getSize());
        String sql = sqlBuilder.toString();
        List<Map<String, Object>> maps = this.jdbcTemplate.queryForList(sql);
        List<PageVO> pageVo = getPageVo(maps);
        res.setList(pageVo);

        String countSql = "select count(1) as cc from receive_msg where " +
                columStr;
        List<Map<String, Object>> maps1 = this.jdbcTemplate.queryForList(countSql);
        Object count = maps1.get(0).get("cc");
        res.setCount((Long) count);
        return res;
    }

    private List<ReceiveMsg> get(List<Map<String, Object>> maps) {

        return maps.stream().map(map -> {
            ReceiveMsg receiveMsg = new ReceiveMsg();
            receiveMsg.setUniqueKey(map.get("unique_key").toString());
            receiveMsg.setPushBody(map.get("push_body").toString());
            receiveMsg.setPushTopic(map.get("push_topic").toString());
            receiveMsg.setPushTag(map.get("push_tag").toString());
            receiveMsg.setExpectPushTime((Long) map.get("expect_push_time"));
            receiveMsg.setReceiveTime((Long) map.get("receive_time"));
            receiveMsg.setId((Long) map.get("id"));
            receiveMsg.setCreateTime((Long) map.get("create_time"));
            receiveMsg.setStatus((Integer) map.get("status"));
            receiveMsg.setResp((String) map.get("resp"));
            receiveMsg.setRealPushTime((Long) map.get("real_push_time"));
            return receiveMsg;
        }).collect(Collectors.toList());
    }

    private List<PageVO> getPageVo(List<Map<String, Object>> maps) {
        return maps.stream().map(map -> {
            PageVO vo = new PageVO();
            vo.setId((Long) map.get("id"));
            vo.setUniqueKey(map.get("unique_key").toString());
            vo.setPushBody(map.get("push_body").toString());
            vo.setPushTopic(map.get("push_topic").toString());
            vo.setPushTag(map.get("push_tag").toString());
            vo.setExpectPushTime(DateUtils.parseTime((Long) map.get("expect_push_time")));
            vo.setReceiveTime(DateUtils.parseTime((Long) map.get("receive_time")));
            vo.setCreateTime(DateUtils.parseTime((Timestamp) map.get("create_time")));
            vo.setStatus((Integer) map.get("status"));
            vo.setResp(Objects.isNull(map.get("resp")) ? "" : (String) map.get("resp"));
            vo.setRealPushTime(
                    Objects.isNull(map.get("real_push_time")) ? "" : DateUtils.parseTime((Long) map.get("real_push_time")));
            return vo;
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println(StatusEnums.getDesc(1));

    }


}
