package com.freddy.ims.netty.service.websocket;

import android.util.Log;

import com.freddy.ims.config.IMSConfiguration;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public class WebSocketReadHandler extends ChannelInboundHandlerAdapter {

    private WebSocketIMSService imsService;

    WebSocketReadHandler(WebSocketIMSService imsService) {
        this.imsService = imsService;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "WebSocketReadHandler handlerAdded() ctx = " + ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "WebSocketReadHandler channelActive() ctx = " + ctx);
        imsService.getHandshaker().handshake(ctx.channel()).syncUninterruptibly();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Log.w(IMSConfiguration.TAG, "WebSocketReadHandler channelInactive() ctx = " + ctx);

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
        Log.e(IMSConfiguration.TAG, "WebSocketReadHandler exceptionCaught() ctx = " + ctx + ", error = " + cause.getMessage());

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
        Log.d(IMSConfiguration.TAG, "WebSocketReadHandler channelRead() ctx = " + ctx + ", msg = " + msg);

        final WebSocketFrame frame = (WebSocketFrame) msg;
        if(frame instanceof TextWebSocketFrame) {
            Log.d(IMSConfiguration.TAG, "frame is TextWebSocketFrame.");
        }else if(frame instanceof PongWebSocketFrame) {
            Log.d(IMSConfiguration.TAG, "frame is PongWebSocketFrame.");
        }else if(frame instanceof CloseWebSocketFrame) {
            Log.d(IMSConfiguration.TAG, "frame is CloseWebSocketFrame.");
        }else if(frame instanceof BinaryWebSocketFrame) {
            Log.d(IMSConfiguration.TAG, "frame is BinaryWebSocketFrame.");
        }
//        // todo 连接成功，回调联调状态
//        imsService.callbackIMSConnectState(IMSConnectState.Connected);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        Log.d(IMSConfiguration.TAG, "WebSocketReadHandler channelReadComplete() ctx = " + ctx);
        ctx.flush();
    }
}
