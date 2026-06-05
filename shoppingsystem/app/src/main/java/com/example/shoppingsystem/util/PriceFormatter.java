package com.example.shoppingsystem.util;

import java.text.DecimalFormat;

/**
 * 价格格式化工具，统一显示为 ¥XX.XX 格式。
 */
public class PriceFormatter {

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static String format(double price) {
        return "¥" + DF.format(price);
    }

    public static String formatPlain(double price) {
        return DF.format(price);
    }
}
