package com.freddy.ims.netty.service.websocket;

import com.freddy.ims.config.TransferProtocol;
import com.freddy.ims.protobuf.MsgProtobuf;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public class WebSocketInitializerHandler extends ChannelInitializer<Channel> {

    private WebSocketIMSService imsService;

    WebSocketInitializerHandler(WebSocketIMSService imsService) {
        this.imsService = imsService;
    }

    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new ChunkedWriteHandler());

        if (imsService.configuration.getTransferProtocol() == TransferProtocol.Protobuf) {
            pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                @Override
                protected void decode(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame, List<Object> list) {
                    ByteBuf buf = frame.content();
                    list.add(buf);
                    buf.retain();
                }
            });
            pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                @Override
                protected void encode(ChannelHandlerContext channelHandlerContext, MessageLiteOrBuilder msg, List<Object> list) {
                    ByteBuf result = null;
                    if (msg instanceof MessageLite) {
                        result = Unpooled.wrappedBuffer(((MessageLite) msg).toByteArray());
                    }
                    if (msg instanceof MessageLite.Builder) {
                        result = Unpooled.wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                    }

                    if(result != null) {
                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        list.add(frame);
                    }
                }
            });
            pipeline.addLast(new ProtobufDecoder(MsgProtobuf.Msg.getDefaultInstance()));
        } else if (imsService.configuration.getTransferProtocol() == TransferProtocol.JSON) {
            pipeline.addLast(new StringEncoder());
            pipeline.addLast(new StringDecoder());
        }

        pipeline.addLast(WebSocketAuthHandler.class.getSimpleName(), new WebSocketAuthHandler(imsService));
        pipeline.addLast(WebSocketReadHandler.class.getSimpleName(), new WebSocketReadHandler(imsService));
    }
}
