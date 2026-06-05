package com.example.shoppingsystem.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式化工具。
 */
public class DateUtils {

    private static final SimpleDateFormat FULL_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat SHORT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat ORDER_NO_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    public static String formatFull(long timestamp) {
        return FULL_FORMAT.format(new Date(timestamp));
    }

    public static String formatShort(long timestamp) {
        return SHORT_FORMAT.format(new Date(timestamp));
    }

    /**
     * 生成订单号：SS + yyyyMMddHHmmss + 4位随机数
     */
    public static String generateOrderNo() {
        String timeStr = ORDER_NO_FORMAT.format(new Date());
        int random = (int) (Math.random() * 10000);
        return "SS" + timeStr + String.format(Locale.getDefault(), "%04d", random);
    }
}
