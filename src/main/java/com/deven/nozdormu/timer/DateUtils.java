package com.deven.nozdormu.timer;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author seven up
 * @date 2023年05月18日 11:16 AM
 */
public class DateUtils {

    public static String parseTime(Long timestamp) {
        if(Objects.isNull(timestamp)){
            return "";
        }
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String parseTime(Timestamp timestamp){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
}
