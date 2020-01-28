package com.freddy.ims.listener;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public interface I_IMSConnectStateListener {

    void onConnecting(String server);

    void onConnected(String server);

    void onUnconnected(String server);
}
