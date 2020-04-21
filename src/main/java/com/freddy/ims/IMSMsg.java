package com.freddy.ims;

/**
 * Created by ChenS on 2019/12/30.
 * chenshichao@outlook.com
 */
public final class IMSMsg {

    private String msgId;
    private String msgType;
    private String fromId;
    private String toId;
    private long timestamp;
    private int status;
    private String content;
    private String contentType;
    private String extension;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "IMSMsg{" +
                "msgId='" + msgId + '\'' +
                ", msgType='" + msgType + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
