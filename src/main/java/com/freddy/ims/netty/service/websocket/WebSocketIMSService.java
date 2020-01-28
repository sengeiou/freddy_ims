package com.freddy.ims.netty.service.websocket;

import android.util.Log;

import com.freddy.ims.IMSMsg;
import com.freddy.ims.config.IMSConfiguration;
import com.freddy.ims.config.IMSConnectState;
import com.freddy.ims.config.TransferProtocol;
import com.freddy.ims.interf.IMSInterface;
import com.freddy.ims.netty.service.AbstractIMSService;
import com.freddy.ims.tools.MsgBuilder;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.internal.StringUtil;

/**
 * Created by ChenS on 2019/12/26.
 * chenshichao@outlook.com
 */
public class WebSocketIMSService extends AbstractIMSService implements IMSInterface {

    private static volatile WebSocketIMSService instance;
    private WebSocketClientHandshaker handshaker;

    private WebSocketIMSService() {
    }

    public static WebSocketIMSService getInstance() {
        if (null == instance) {
            synchronized (WebSocketIMSService.class) {
                if (null == instance) {
                    instance = new WebSocketIMSService();
                }
            }
        }

        return instance;
    }

    @Override
    public void connect() {
        isClosed = false;
        reconnect(true);
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        isClosed = true;
        configuration = null;
        isReconnecting = false;

        closeChannel();
        closeBootstrap();
        destroyExecutorService();
    }

    @Override
    public void sendMsg(IMSMsg msg) {
        if (configuration == null) {
            Log.e(IMSConfiguration.TAG, "发送消息失败，reason : IMSConfiguration is null.");
            return;
        }

        if (channel == null) {
            Log.e(IMSConfiguration.TAG, "发送消息失败，reason : Channel is null.");
            return;
        }

        TransferProtocol transferProtocol = configuration.getTransferProtocol();
        if (transferProtocol == TransferProtocol.Protobuf) {
            channel.writeAndFlush(MsgBuilder.convertToProtobufMsg(msg));
        } else if (transferProtocol == TransferProtocol.JSON) {
            channel.writeAndFlush(new TextWebSocketFrame(MsgBuilder.convertToJsonMsg(msg)));
        }
    }

    private void initBootstrap() {
        EventLoopGroup loopGroup = new NioEventLoopGroup(4);
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, configuration.getConnectTimeout())
                .handler(new WebSocketInitializerHandler(this));
    }

    void reconnect(boolean isFirstConnect) {
        if (!isFirstConnect) {
            try {
                Thread.sleep(configuration.getReconnectIntervalTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isClosed && !isReconnecting) {
            synchronized (this) {
                if (!isClosed && !isReconnecting) {
                    isReconnecting = true;
                    callbackIMSConnectState(IMSConnectState.Connecting);
                    closeChannel();
                    executorService.execBossTask(new ReconnectTask(isFirstConnect));
                }
            }
        }
    }

    private void closeChannel() {
        try {
            if (channel != null) {
                try {
                    // todo removeHandler
                } finally {
                    try {
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        channel.eventLoop().shutdownGracefully();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(IMSConfiguration.TAG, "关闭channel出错， reason : " + e.getMessage());
        } finally {
            channel = null;
        }
    }

    private void closeBootstrap() {
        try {
            if (bootstrap != null) {
                bootstrap.group().shutdownGracefully();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bootstrap = null;
        }
    }

    private void updateHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    WebSocketClientHandshaker getHandshaker() {
        return this.handshaker;
    }

    private final class ReconnectTask implements Runnable {

        private boolean isFirstConnect;

        ReconnectTask(boolean isFirstConnect) {
            this.isFirstConnect = isFirstConnect;
        }

        @Override
        public void run() {
            if (!isFirstConnect) {
                callbackIMSConnectState(IMSConnectState.Unconnected);
            }

            try {
                // 重连时，释放工作线程组，也就是停止心跳
                executorService.destroyWorkLoopGroup();

                while (!isClosed) {
                    if (!isNetworkAvailable()) {
                        Log.d(IMSConfiguration.TAG, "网络不可用，等待3秒后自动重试...");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    IMSConnectState connectState;
                    if ((connectState = reconnect()) == IMSConnectState.Connected) {
                        //                        callbackIMSConnectState(IMSConnectState.Connected);
                        //                        if (mWebSocketHandshakeTask == null) {
                        //                            mWebSocketHandshakeTask = new WebSocketHandshakeTask();
                        //                        }
                        //                        channel.eventLoop().execute(mWebSocketHandshakeTask);
                        break;
                    }

                    if (connectState == IMSConnectState.Unconnected) {
                        callbackIMSConnectState(IMSConnectState.Unconnected);
                        try {
                            Thread.sleep(configuration.getReconnectIntervalTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                isReconnecting = false;
            }
        }

        private IMSConnectState reconnect() {
            if (isClosed) {
                return IMSConnectState.Unconnected;
            }
            closeBootstrap();
            initBootstrap();
            return connectServer();
        }

        private IMSConnectState connectServer() {
            String[] serverList = configuration.getServerList();
            if (serverList == null || serverList.length == 0) {
                return IMSConnectState.Unconnected;
            }

            for (int i = 0; (!isClosed && i < serverList.length); i++) {
                String server = serverList[i];
                if (StringUtil.isNullOrEmpty(server)) {
                    Log.e(IMSConfiguration.TAG, "连接失败，server地址为空");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                URI uri;
                try {
                    uri = URI.create(server);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.e(IMSConfiguration.TAG, String.format("连接失败，server[%s]地址不合法", server));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }

                if (!"ws".equals(uri.getScheme())) {
                    Log.e(IMSConfiguration.TAG, String.format("连接失败，server[%s]地址不合法", server));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                for (int j = 1; j <= configuration.getReconnectCount(); j++) {
                    if (isClosed || !isNetworkAvailable()) {
                        return IMSConnectState.Unconnected;
                    }

                    if (connectState != IMSConnectState.Connecting) {
                        callbackIMSConnectState(IMSConnectState.Connecting);
                    }

                    Log.d(IMSConfiguration.TAG, String.format("正在进行『%s』的第『%d』次连接，当前重连延时时长为『%dms』", server, j, j * configuration.getReconnectIntervalTime()));
                    currentServer = server;
                    try {
                        String host = uri.getHost();
                        int port = uri.getPort();
                        updateHandshaker(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, EmptyHttpHeaders.INSTANCE, 1280000));
                        toServer(host, port);
                        if (channel != null && channel.isActive() && channel.isOpen() && channel.isRegistered()) {
                            Log.d(IMSConfiguration.TAG, String.format("连接Server『%s』成功，准备进行认证...", currentServer));
                            return IMSConnectState.Connected;
                        } else {
                            Thread.sleep(j * configuration.getReconnectIntervalTime());
                        }
                    } catch (InterruptedException e) {
                        close();
                        break;// 线程被中断，强制关闭
                    }
                }
            }

            // 执行到这里，代表连接失败
            return IMSConnectState.Unconnected;
        }
    }

    private void toServer(String host, int port) {
        try {
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Log.e(IMSConfiguration.TAG, String.format("连接Server『%s』失败", currentServer));
            channel = null;
        }
    }
}
