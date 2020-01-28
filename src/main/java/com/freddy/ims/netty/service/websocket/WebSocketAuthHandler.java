package com.freddy.ims.netty.service.websocket;

import android.util.Log;

import com.freddy.ims.config.IMSConfiguration;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;

/**
 * Created by ChenS on 2019/12/31.
 * chenshichao@outlook.com
 */
public class WebSocketAuthHandler extends ChannelInboundHandlerAdapter {

    private WebSocketIMSService imsService;
    private ChannelPromise handshakePromise;

    WebSocketAuthHandler(WebSocketIMSService imsService) {
        this.imsService = imsService;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "WebSocketAuthHandler handlerAdded() ctx = " + ctx);
        handshakePromise = ctx.newPromise();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if(!handshakePromise.isDone()) {
            handshakePromise.setFailure(new Throwable("handshake failure."));
        }else {
            ctx.fireChannelInactive();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(!handshakePromise.isDone()) {
            handshakePromise.setFailure(new Throwable("handshake failure."));
        }else {
            ctx.fireExceptionCaught(cause);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Log.d(IMSConfiguration.TAG, "WebSocketAuthHandler channelRead() ctx = " + ctx + ", msg = " + msg);
        Channel channel = ctx.channel();
        if(!imsService.getHandshaker().isHandshakeComplete()) {
            try {
                Log.d(IMSConfiguration.TAG, "WebSocket 握手成功.");
                imsService.getHandshaker().finishHandshake(channel, (FullHttpResponse) msg);
                handshakePromise.setSuccess();
                // 握手成功，进行认证
                imsService.auth();
            }catch (WebSocketHandshakeException e) {
                Log.e(IMSConfiguration.TAG, "WebSocket 握手失败, reason : " + e.getMessage());
                handshakePromise.setFailure(e);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
