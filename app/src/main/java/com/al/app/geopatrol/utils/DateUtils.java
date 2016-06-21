package com.al.app.geopatrol.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dai Jingjing on 2016/2/26.
 */
public class DateUtils {

    private static final String MSG_TODAY = "今天";
    private static final String MSG_YESTERDAY = "昨天";
    private static final String MSG_BEFORE_YESTERDAY = "前天";
    private static final String MSG_EEE = "EEE";
    private static final String MSG_BEFORE_EEE = "上EEE";
    private static final String MSG_M_D = "M月d日";
    private static final String MSG_LAST_M_D = "去年M月d日";
    private static final String MSG_BREFORE_LAST_M_D = "前年M月d日";

    private static final String MSG_TIME_AM = "上午";
    private static final String MSG_TIME_PM = "下午";
    private static final String MSG_TIME_NOON = "中午";
    private static final String MSG_TIME_ADJUST = "刚刚";
    private static final String MSG_TIME_MIN = "1分钟以前";
    private static final String MSG_TIME_HOUR = "1小时以前";
    private static final String MSG_TIME_15 = "15分钟前";
    private static final String MSG_TIME_30 = "半小时前";
    private static final String MSG_TIME_DAY = "1天以前";
    private static final String MSG_TIME_MONTH = "1个月以前";
    private static final String MSG_TIME_YEAR = "1年以前";
    private static final String MSG_0_4 = "凌晨";
    private static final String MSG_5_8 = "早晨";
    private static final String MSG_9_11 = "上午";
    private static final String MSG_12 = "中午";
    private static final String MSG_13_17 = "下午";
    private static final String MSG_18_23 = "晚上";

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_MONTH = 30 * ONE_DAY;
    public static final long ONE_YEAR = 365 * ONE_DAY;

    public static String formatDate(String format, Date date) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static Date parseDate(String format, String date) throws ParseException {
        return new SimpleDateFormat(format, Locale.getDefault()).parse(date);
    }

    public static Date parseJSONDate(String s_date) throws ParseException {
        if (s_date == null)
            return new Date(0);

        return parseDate("yyyy-MM-dd'T'HH:mm:ss'Z'Z", s_date);
    }

    public static Date parseJSONDate(String s_date, Date defaultValue) {
        try {
            return parseJSONDate(s_date);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public static String toJSONDate(Date date) {
        return formatDate("yyyy-MM-dd'T'HH:mm:ss'Z'", date);
    }

    public static Date parseLongDate(String s_date) throws ParseException {
        if (s_date == null)
            return new Date(0);

        return parseDate("yyyy-MM-dd HH:mm:ss", s_date);
    }

    public static String toBeauty(Date date) {
        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar d = Calendar.getInstance(Locale.getDefault());
        d.setTime(date);
        if (now.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)) {
            return toBeautyTime(date);
        }

        return toBeautyDate(date) + " " + toBeautyTime(date);
    }

    public static String toBeautyDate(Date date) {
        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar d = Calendar.getInstance(Locale.getDefault());
        d.setTime(date);

        if (now.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)) {

            return MSG_TODAY;
        }
        if (now.get(Calendar.DAY_OF_YEAR) - 1 == d.get(Calendar.DAY_OF_YEAR)) {

            return MSG_YESTERDAY;
        }
        if (now.get(Calendar.DAY_OF_YEAR) - 2 == d.get(Calendar.DAY_OF_YEAR)) {

            return MSG_BEFORE_YESTERDAY;
        }
        if (now.get(Calendar.WEEK_OF_YEAR) == d.get(Calendar.WEEK_OF_YEAR)) {

            return formatDate(MSG_EEE, date);
        }
        if (now.get(Calendar.WEEK_OF_YEAR) - 1 == d.get(Calendar.WEEK_OF_YEAR)) {

            return formatDate(MSG_BEFORE_EEE, date);
        }

        if (now.get(Calendar.YEAR) == d.get(Calendar.YEAR)) {

            return formatDate(MSG_M_D, date);
        }
        if (now.get(Calendar.YEAR) - 1 == d.get(Calendar.YEAR)) {

            return formatDate(MSG_LAST_M_D, date);
        }
        if (now.get(Calendar.YEAR) - 2 == d.get(Calendar.YEAR)) {

            return formatDate(MSG_BREFORE_LAST_M_D, date);
        }

        return date.getTime() > 0 ? formatDate("yy年M月d日", date) : "很久";

    }

    static final String[] TIME_PREFIX = new String[] {
            MSG_0_4, MSG_0_4, MSG_0_4, MSG_0_4, MSG_0_4, // 0~4
            MSG_5_8, MSG_5_8, MSG_5_8, MSG_5_8, // 5~8
            MSG_9_11, MSG_9_11, MSG_9_11, // 9~11
            MSG_12, // 12
            MSG_13_17, MSG_13_17, MSG_13_17, MSG_13_17, MSG_13_17, // 13~17
            MSG_18_23, MSG_18_23, MSG_18_23, MSG_18_23, MSG_18_23, MSG_18_23 // 18~23
    };

    public static String toBeautyTime(Date date) {
        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar d = Calendar.getInstance(Locale.getDefault());
        d.setTime(date);

        if (now.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)
                && now.get(Calendar.HOUR_OF_DAY) == d.get(Calendar.DAY_OF_YEAR)) {
            if (now.get(Calendar.MINUTE) - 10 >= d.get(Calendar.MINUTE))
                return MSG_TIME_ADJUST;
            if (now.get(Calendar.MINUTE) - 30 >= d.get(Calendar.MINUTE))
                return MSG_TIME_15;
            if (now.get(Calendar.MINUTE) - 60 >= d.get(Calendar.MINUTE))
                return MSG_TIME_30;
            if (now.get(Calendar.MINUTE) - 120 >= d.get(Calendar.MINUTE))
                return MSG_TIME_HOUR;
        }

        int hour = d.get(Calendar.HOUR_OF_DAY);

        return date.getTime() > 0 ? TIME_PREFIX[hour] + formatDate(" h:mm", date) : "很久以前";
    }

    public static String toDate(Date date) throws ParseException {
        return formatDate("MM-dd", date);
    }

    public static String toTime(Date time) throws ParseException {
        return formatDate("HH:mm", time);
    }

    public static String toLongDate(Date time) throws ParseException {
        return formatDate("yyyy-MM-dd HH:mm:ss", time);
    }

    public static String toShortDate(Date time) throws ParseException {
        return formatDate("yyyy-MM-dd HH:mm", time);
    }

    public static String toTimestamp(Date time) {
        return formatDate("yyyyMMddHHmmss", time);
    }

    public static Date addDays(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + days);
        return c.getTime();
    }

}
