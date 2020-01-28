package com.freddy.ims.config;

import com.freddy.ims.listener.I_IMSConnectStateListener;

import java.util.Arrays;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public class IMSConfiguration {

    public static final String TAG = "freddy_ims";
    // 通信协议，默认TCP
    private CommunicationProtocol communicationProtocol = CommunicationProtocol.TCP;
    // 传输协议，默认Protobuf
    private TransferProtocol transferProtocol = TransferProtocol.Protobuf;
    // 连接超时时长，默认10秒
    private int connectTimeout = 10 * 1000;
    // 重连次数，默认3次
    private int reconnectCount = 3;
    // 重连超时时长，默认8秒
    private int reconnectIntervalTime = 8 * 1000;
    // 心跳间隔时间，默认30秒
    private int heartbeatIntervalTime = 30 * 1000;
    // 是否开启自动重发消息，默认开启
    private boolean enableAutoResend = true;
    // 重发次数，默认3次
    private int resendCount = 3;
    // 重发间隔时间，默认8秒
    private int resendIntervalTime = 8000;
    // 服务器列表，支持多个服务器，TCP格式：["192.168.1.1 8080", "192.168.1.2 8081"]，WebSocket格式：["ws:192.168.1.1:8080", "ws:192.168.1.2:8081"]
    private String[] serverList;
    // IMS连接状态回调，应用层实现此接口
    private I_IMSConnectStateListener imsConnectStateListener;
    // 认证消息类型
    private int authMsgType;
    // 心跳消息类型
    private int heartbeatMsgType;
    // 用户token，用于认证
    private String token;

    public CommunicationProtocol getCommunicationProtocol() {
        return communicationProtocol;
    }

    private void setCommunicationProtocol(CommunicationProtocol protocol) {
        this.communicationProtocol = protocol;
    }

    public TransferProtocol getTransferProtocol() {
        return transferProtocol;
    }

    private void setTransferProtocol(TransferProtocol protocol) {
        this.transferProtocol = protocol;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    private void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReconnectCount() {
        return reconnectCount;
    }

    public void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    public int getReconnectIntervalTime() {
        return reconnectIntervalTime;
    }

    private void setReconnectIntervalTime(int reconnectIntervalTime) {
        this.reconnectIntervalTime = reconnectIntervalTime;
    }

    public int getHeartbeatIntervalTime() {
        return heartbeatIntervalTime;
    }

    private void setHeartbeatIntervalTime(int heartbeatIntervalTime) {
        this.heartbeatIntervalTime = heartbeatIntervalTime;
    }

    public boolean isEnableAutoResend() {
        return enableAutoResend;
    }

    private void setEnableAutoResend(boolean enableAutoResend) {
        this.enableAutoResend = enableAutoResend;
    }

    public int getResendCount() {
        return resendCount;
    }

    private void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public int getResendIntervalTime() {
        return resendIntervalTime;
    }

    private void setResendIntervalTime(int resendIntervalTime) {
        this.resendIntervalTime = resendIntervalTime;
    }

    public String[] getServerList() {
        return serverList;
    }

    public I_IMSConnectStateListener getImsConnectStateListener() {
        return imsConnectStateListener;
    }

    private void setImsConnectStateListener(I_IMSConnectStateListener imsConnectStateListener) {
        this.imsConnectStateListener = imsConnectStateListener;
    }

    private void setServerList(String... serverList) {
        this.serverList = serverList;
    }

    public int getAuthMsgType() {
        return authMsgType;
    }

    private void setAuthMsgType(int authMsgType) {
        this.authMsgType = authMsgType;
    }

    public int getHeartbeatMsgType() {
        return heartbeatMsgType;
    }

    private void setHeartbeatMsgType(int heartbeatMsgType) {
        this.heartbeatMsgType = heartbeatMsgType;
    }

    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    public static class Builder {

        private IMSConfiguration configuration;

        public Builder() {
            configuration = new IMSConfiguration();
        }

        public Builder setCommunicationProtocol(CommunicationProtocol protocol) {
            configuration.setCommunicationProtocol(protocol);
            return this;
        }

        public Builder setTransferProtocol(TransferProtocol protocol) {
            configuration.setTransferProtocol(protocol);
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            configuration.setConnectTimeout(connectTimeout);
            return this;
        }

        public Builder setReconnectCount(int reconnectCount) {
            configuration.setReconnectCount(reconnectCount);
            return this;
        }

        public Builder setReconnectIntervalTime(int reconnectIntervalTime) {
            configuration.setReconnectIntervalTime(reconnectIntervalTime);
            return this;
        }

        public Builder setHeartbeatIntervalTime(int heartbeatIntervalTime) {
            configuration.setHeartbeatIntervalTime(heartbeatIntervalTime);
            return this;
        }

        public Builder setEnableAutoResend(boolean enableAutoResend) {
            configuration.setEnableAutoResend(enableAutoResend);
            return this;
        }

        public Builder setResendCount(int resendCount) {
            configuration.setResendCount(resendCount);
            return this;
        }

        public Builder setResendIntervalTime(int resendIntervalTime) {
            configuration.setResendIntervalTime(resendIntervalTime);
            return this;
        }

        public Builder setServerList(String... serverList) {
            configuration.setServerList(serverList);
            return this;
        }

        public Builder setImsConnectStateListener(I_IMSConnectStateListener listener) {
            configuration.setImsConnectStateListener(listener);
            return this;
        }

        public Builder setAuthMsgType(int authMsgType) {
            configuration.setAuthMsgType(authMsgType);
            return this;
        }

        public Builder setHeartbeatMsgType(int heartbeatMsgType) {
            configuration.setHeartbeatMsgType(heartbeatMsgType);
            return this;
        }

        public Builder setToken(String token) {
            configuration.setToken(token);
            return this;
        }

        public IMSConfiguration build() {
            return configuration;
        }
    }

    @Override
    public String toString() {
        return "IMSConfiguration\n{" + "\n" +
                "\t" + "communicationProtocol=" + communicationProtocol + "\n" +
                "\t" + "transferProtocol=" + transferProtocol + "\n" +
                "\t" + "connectTimeout=" + connectTimeout + "\n" +
                "\t" + "reconnectCount=" + reconnectCount + "\n" +
                "\t" + "reconnectIntervalTime=" + reconnectIntervalTime + "\n" +
                "\t" + "heartbeatIntervalTime=" + heartbeatIntervalTime + "\n" +
                "\t" + "enableAutoResend=" + enableAutoResend + "\n" +
                "\t" + "resendCount=" + resendCount + "\n" +
                "\t" + "resendIntervalTime=" + resendIntervalTime + "\n" +
                "\t" + "serverList=" + Arrays.toString(serverList) + "\n" +
                "\t" + "imsConnectStateListener=" + imsConnectStateListener + "\n" +
                "\t" + "authMsgType=" + authMsgType + "\n" +
                "\t" + "heartbeatMsgType=" + heartbeatMsgType + "\n" +
                '}';
    }
}
