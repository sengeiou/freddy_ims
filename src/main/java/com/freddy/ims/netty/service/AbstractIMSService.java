package com.freddy.ims.netty.service;

import android.util.Log;

import com.freddy.ims.IMSKit;
import com.freddy.ims.IMSMsg;
import com.freddy.ims.config.IMSConfiguration;
import com.freddy.ims.config.IMSConnectState;
import com.freddy.ims.interf.IMSInterface;
import com.freddy.ims.listener.I_IMSConnectStateListener;
import com.freddy.ims.tools.ExecutorServiceFactory;
import com.google.gson.JsonObject;

import java.util.UUID;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public abstract class AbstractIMSService implements IMSInterface {

    protected Bootstrap bootstrap;
    protected Channel channel;
    public IMSConfiguration configuration;
    protected IMSConnectState connectState = IMSConnectState.Unconnected;
    protected String currentServer;
    protected boolean isClosed = true;
    protected boolean isReconnecting = false;
    protected ExecutorServiceFactory executorService;

    @Override
    public IMSInterface init(IMSConfiguration configuration) {
        this.configuration = configuration;
        executorService = new ExecutorServiceFactory();
        executorService.initBossLoopGroup();// 初始化重连线程组
        return this;
    }

    @Override
    public IMSConnectState getConnectState() {
        return connectState;
    }

    public void callbackIMSConnectState(IMSConnectState connectState) {
        this.connectState = connectState;
        if (configuration == null) {
            Log.e(IMSConfiguration.TAG, getClass().getSimpleName() + " callbackIMSConnectState() failure, reason : IMSConfiguration is null.");
            return;
        }

        I_IMSConnectStateListener imsConnectStateListener = configuration.getImsConnectStateListener();
        if (imsConnectStateListener == null) {
            Log.e(IMSConfiguration.TAG, getClass().getSimpleName() + " callbackIMSConnectState() failure, reason : I_IMSConnectStateListener is null.");
            return;
        }

        if (connectState == IMSConnectState.Connecting) {
            imsConnectStateListener.onConnecting(currentServer);
        } else if (connectState == IMSConnectState.Connected) {
            imsConnectStateListener.onConnected(currentServer);
        } else {
            imsConnectStateListener.onUnconnected(currentServer);
        }
    }

    protected boolean isNetworkAvailable() {
        return IMSKit.getInstance().isNetworkAvailable();
    }

    protected void destroyExecutorService() {
        try {
            if (executorService != null) {
                executorService.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void auth() {
        Log.d(IMSConfiguration.TAG, getClass().getSimpleName() + " 开始认证...");
        IMSMsg msg = new IMSMsg();
        msg.setMsgId(UUID.randomUUID().toString());
        msg.setMsgType(configuration.getAuthMsgType());
        msg.setTimestamp(System.currentTimeMillis());
        JsonObject extension = new JsonObject();
        extension.addProperty(IMSConfiguration.KEY_TOKEN, configuration.getToken());
        msg.setExtension(extension.toString());

        sendMsg(msg);
    }
}
