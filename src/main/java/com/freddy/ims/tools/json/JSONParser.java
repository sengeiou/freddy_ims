package com.freddy.ims.tools.json;

import java.util.List;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public final class JSONParser {

    public static <T> String toJSONString(T t) {
        return JSONParserFactory.getJSONParser().toJSONString(t);
    }

    public static Object toJSONObject(String json) {
        return JSONParserFactory.getJSONParser().toJSONObject(json);
    }

    public static Object toJSONArray(String json) {
        return JSONParserFactory.getJSONParser().toJSONArray(json);
    }

    public static <T> T parseObject(String json, Class<T> cls) {
        return JSONParserFactory.getJSONParser().parseObject(json, cls);
    }

    public static <T> List<T> parseArray(String json, Class<T> cls) {
        return JSONParserFactory.getJSONParser().parseArray(json, cls);
    }
}
