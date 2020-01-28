package com.freddy.ims;

import android.util.Log;

import com.freddy.ims.config.CommunicationProtocol;
import com.freddy.ims.config.IMSConfiguration;
import com.freddy.ims.config.IMSConnectState;
import com.freddy.ims.config.TransferProtocol;
import com.freddy.ims.interf.IMSInterface;
import com.freddy.ims.netty.service.tcp.TCPIMSService;
import com.freddy.ims.netty.service.websocket.WebSocketIMSService;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public class IMSKit {

    private IMSConfiguration configuration;
    private IMSInterface mIMSService;
    private boolean networkAvailable;// 网络是否可用

    private IMSKit() {}

    public static IMSKit getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final IMSKit INSTANCE = new IMSKit();
    }

    public IMSKit init(IMSConfiguration configuration) {
        this.configuration = configuration;
        printConfiguration();
        return this;
    }

    public void start() {
        if (configuration == null) {
            Log.e(IMSConfiguration.TAG, "IMSKit start() failure, reason : IMSConfiguration is null.");
            return;
        }

        CommunicationProtocol communicationProtocol = configuration.getCommunicationProtocol();
        if (communicationProtocol == CommunicationProtocol.TCP) {
            mIMSService = TCPIMSService.getInstance();
        } else if (communicationProtocol == CommunicationProtocol.WebSocket) {
            mIMSService = WebSocketIMSService.getInstance();
        }

        TransferProtocol transferProtocol = configuration.getTransferProtocol();
        if(transferProtocol == TransferProtocol.Protobuf) {

        }else if(transferProtocol == TransferProtocol.JSON) {

        }

        if(mIMSService.getConnectState() == IMSConnectState.Connected) {
            Log.w(IMSConfiguration.TAG, "IMS 已连接.");
            return;
        }

        mIMSService.init(configuration).connect();
    }

    public void stop() {
        if(mIMSService != null) {
            if(mIMSService.getConnectState() == IMSConnectState.Unconnected) {
                Log.w(IMSConfiguration.TAG, "IMS 未连接.");
                return;
            }

            mIMSService.close();
        }
    }

    public void setNetworkAvailable(boolean networkAvailable) {
        this.networkAvailable = networkAvailable;
    }

    public boolean isNetworkAvailable() {
//        return this.networkAvailable;
        return true;
    }

    private void printConfiguration() {
        Log.d(IMSConfiguration.TAG, "configuration=" + configuration);
    }
}
