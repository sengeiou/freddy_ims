package com.freddy.ims.tools.json;

import com.freddy.ims.tools.json.gson.GsonParser;
import com.freddy.ims.tools.json.interf.I_JSONParser;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
final class JSONParserFactory {

    static I_JSONParser getJSONParser() {
        return GsonParser.getInstance();
    }
}
