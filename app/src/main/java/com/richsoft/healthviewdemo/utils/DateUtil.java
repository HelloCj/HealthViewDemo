package com.richsoft.healthviewdemo.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 作者：chengjie on 17/3/29 09:00
 * 邮箱：10078216660@qq.com
 * 描述：
 */
public class DateUtil {
    private DateUtil() {
    }

    /**
     * 获取当前系统的时间的时,分
     *
     * @return 时间
     */
    public static String getCurrentHoursAndMin() {
        int hours = Calendar.getInstance().get(Calendar.HOUR);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        return hours + ":" + (min < 10 ? "0" + min : min);
    }

    /**
     * 获取最近7日的日期
     *
     * @return 日期
     */
    public static List<String> getCurrent7Days() {
        List<String> dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String date = format.format(calendar.getTime());
        dayList.add(date);
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = format.format(calendar.getTime());
            dayList.add(date);
        }
        Collections.reverse(dayList);
        return dayList;
    }
}
