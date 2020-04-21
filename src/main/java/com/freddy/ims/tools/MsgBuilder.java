package com.freddy.ims.tools;

import android.text.TextUtils;

import com.freddy.ims.IMSMsg;
import com.freddy.ims.protobuf.MsgProtobuf;
import com.freddy.ims.tools.json.JSONParser;

import io.netty.util.internal.StringUtil;

/**
 * Created by ChenS on 2019/12/30.
 * chenshichao@outlook.com
 */
public final class MsgBuilder {

    public static MsgProtobuf.Msg convertToProtobufMsg(IMSMsg msg) {
        if (msg == null) {
            return null;
        }

        MsgProtobuf.Head.Builder headBuilder = MsgProtobuf.Head.newBuilder();
        MsgProtobuf.Body.Builder bodyBuilder = MsgProtobuf.Body.newBuilder();

        String msgId = msg.getMsgId();
        if (!TextUtils.isEmpty(msgId)) {
            headBuilder.setMsgId(msgId);
        }

        String msgType = msg.getMsgType();
        if (!StringUtil.isNullOrEmpty(msgType)) {
            headBuilder.setMsgType(msgType);
        }

        String fromId = msg.getFromId();
        if (!TextUtils.isEmpty(fromId)) {
            headBuilder.setFromId(fromId);
        }

        String toId = msg.getToId();
        if (!TextUtils.isEmpty(toId)) {
            headBuilder.setToId(toId);
        }

        long timestamp = msg.getTimestamp();
        if (timestamp != 0) {
            headBuilder.setTimestamp(timestamp);
        }

        int status = msg.getStatus();
        if (status != 0) {
            headBuilder.setStatus(status);
        }

        String contentType = msg.getContentType();
        if (!StringUtil.isNullOrEmpty(contentType)) {
            bodyBuilder.setContentType(contentType);
        }

        String content = msg.getContent();
        if (!TextUtils.isEmpty(content)) {
            bodyBuilder.setContent(content);
        }

        String extension = msg.getExtension();
        if(!StringUtil.isNullOrEmpty(extension)) {
            bodyBuilder.setExtension(extension);
        }

        MsgProtobuf.Msg.Builder msgBuilder = MsgProtobuf.Msg.newBuilder();
        msgBuilder.setHead(headBuilder.build());
        msgBuilder.setBody(bodyBuilder.build());
        return msgBuilder.build();
    }

    public static IMSMsg convertToIMSMsg(MsgProtobuf.Msg protobufMsg) {
        if (protobufMsg == null) {
            return null;
        }

        IMSMsg msg = new IMSMsg();

        MsgProtobuf.Head head = protobufMsg.hasHead() ? protobufMsg.getHead() : null;
        if (head != null) {
            msg.setMsgId(head.getMsgId());
            msg.setMsgType(head.getMsgType());
            msg.setFromId(head.getFromId());
            msg.setToId(head.getToId());
            msg.setTimestamp(head.getTimestamp());
            msg.setStatus(head.getStatus());
        }

        MsgProtobuf.Body body = protobufMsg.hasBody() ? protobufMsg.getBody() : null;
        if (body != null) {
            msg.setContent(body.getContent());
            msg.setContentType(body.getContentType());
            msg.setExtension(body.getExtension());
        }

        return msg;
    }

    public static String convertToJsonMsg(IMSMsg msg) {
        if (msg == null) {
            return null;
        }

        try {
            return JSONParser.toJSONString(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static IMSMsg convertToIMSMsg(String jsonMsg) {
        if (TextUtils.isEmpty(jsonMsg)) {
            return null;
        }

        try {
            return JSONParser.parseObject(jsonMsg, IMSMsg.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
