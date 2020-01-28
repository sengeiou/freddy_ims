package com.freddy.ims.interf;

import com.freddy.ims.IMSMsg;
import com.freddy.ims.config.IMSConfiguration;
import com.freddy.ims.config.IMSConnectState;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public interface IMSInterface {

    IMSInterface init(IMSConfiguration configuration);

    void connect();

    void close();

    IMSConnectState getConnectState();

    void sendMsg(IMSMsg msg);
}
