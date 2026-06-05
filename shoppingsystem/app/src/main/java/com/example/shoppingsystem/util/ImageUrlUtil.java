package com.example.shoppingsystem.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片 URL 列表与 JSON 字符串之间的转换工具。
 */
public class ImageUrlUtil {

    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    /**
     * 将图片 URL 列表序列化为 JSON 字符串。
     */
    public static String toJson(List<String> urls) {
        if (urls == null || urls.isEmpty()) return "[]";
        return GSON.toJson(urls);
    }

    /**
     * 将 JSON 字符串反序列化为图片 URL 列表。
     */
    public static List<String> fromJson(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return GSON.fromJson(json, LIST_TYPE);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取第一张图片 URL，若无则返回空字符串。
     */
    public static String getFirstImage(String json) {
        List<String> urls = fromJson(json);
        return urls.isEmpty() ? "" : urls.get(0);
    }
}
