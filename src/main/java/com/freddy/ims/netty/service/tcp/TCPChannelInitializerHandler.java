package com.freddy.ims.netty.service.tcp;

import com.freddy.ims.config.TransferProtocol;
import com.freddy.ims.protobuf.MsgProtobuf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by ChenS on 2019/12/27.
 * chenshichao@outlook.com
 */
public class TCPChannelInitializerHandler extends ChannelInitializer<Channel> {

    private TCPIMSService imsService;

    public TCPChannelInitializerHandler(TCPIMSService imsService) {
        this.imsService = imsService;
    }

    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("fremeEncoder", new LengthFieldPrepender(2));
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));

        if (imsService.configuration.getTransferProtocol() == TransferProtocol.Protobuf) {
            pipeline.addLast(new ProtobufEncoder());
            pipeline.addLast(new ProtobufDecoder(MsgProtobuf.Msg.getDefaultInstance()));
        } else if (imsService.configuration.getTransferProtocol() == TransferProtocol.JSON) {
            pipeline.addLast(new StringEncoder());
            pipeline.addLast(new StringDecoder());
        }

        pipeline.addLast(TCPReadHandler.class.getSimpleName(), new TCPReadHandler(imsService));
    }
}
