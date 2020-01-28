package com.freddy.ims.tools.json.gson;

import android.text.TextUtils;

import com.freddy.ims.tools.json.interf.I_JSONParser;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public final class GsonParser implements I_JSONParser {

    private Gson gson;

    private GsonParser() {
        gson = new Gson();
    }

    public static GsonParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final GsonParser INSTANCE = new GsonParser();
    }

    @Override
    public <T> String toJSONString(T t) {
        if (t == null) {
            return null;
        }

        return gson.toJson(t);
    }

    @Override
    public Object toJSONObject(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        return JsonParser.parseString(json).getAsJsonObject();
    }

    @Override
    public Object toJSONArray(String json) {
        return JsonParser.parseString(json).getAsJsonArray();
    }

    @Override
    public <T> T parseObject(String json, Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        if (cls == null) {
            return null;
        }

        try {
            return gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> parseArray(String json, Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        if (cls == null) {
            return null;
        }

        try {
            return gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
