package com.freddy.ims.netty.service.tcp;

import android.util.Log;

import com.freddy.ims.config.IMSConfiguration;
import com.freddy.ims.config.IMSConnectState;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public class TCPReadHandler extends ChannelInboundHandlerAdapter {

    private TCPIMSService imsService;

    TCPReadHandler(TCPIMSService imsService) {
        this.imsService = imsService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "TCPReadHandler channelActive() ctx = " + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Log.w(IMSConfiguration.TAG, "TCPReadHandler channelInactive() ctx = " + ctx);
        Channel channel = ctx.channel();
        if(channel != null) {
            channel.close();
            ctx.close();
        }

        // 重连
        imsService.reconnect(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Log.e(IMSConfiguration.TAG, "TCPReadHandler exceptionCaught() ctx = " + ctx + ", error = " + cause.getMessage());
        Channel channel = ctx.channel();
        if(channel != null) {
            channel.close();
            ctx.close();
        }

        // 重连
        imsService.reconnect(false);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Log.d(IMSConfiguration.TAG, "TCPReadHandler channelRead() ctx = " + ctx + ", msg = " + msg);

        // todo 连接成功，回调联调状态
        imsService.callbackIMSConnectState(IMSConnectState.Connected);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "TCPReadHandler channelReadComplete() ctx = " + ctx);
    }
}
