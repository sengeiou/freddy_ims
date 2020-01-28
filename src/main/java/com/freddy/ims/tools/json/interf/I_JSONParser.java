package com.freddy.ims.tools.json.interf;

import java.util.List;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public interface I_JSONParser {

    <T> String toJSONString(T t);

    Object toJSONObject(String json);

    Object toJSONArray(String json);

    <T> T parseObject(String json, Class<T> cls);

    <T> List<T> parseArray(String json, Class<T> cls);
}
