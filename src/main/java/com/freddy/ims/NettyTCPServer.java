package com.freddy.ims;

import com.freddy.ims.protobuf.MsgProtobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyTCPServer {

    public static void main(String[] args) {
        NettyTCPServer.getInstance().start();
    }

    private static NettyTCPServer instance;

    private NettyTCPServer() {
    }

    public static NettyTCPServer getInstance() {
        if (null == instance) {
            synchronized (NettyTCPServer.class) {
                if (null == instance) {
                    instance = new NettyTCPServer();
                }
            }
        }

        return instance;
    }

    public void start() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossLoopGroup, workLoopGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            pipeline.addLast(new ProtobufEncoder()).addLast(new ProtobufDecoder(MsgProtobuf.Msg.getDefaultInstance()));
                            pipeline.addLast(new TCPReadHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(8585).sync();
            System.out.println("the tcp server started.");
//            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }
    }

    private static final class TCPReadHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            System.out.println("channelActive() ctx=" + ctx.toString());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            System.out.println("channelInactive() ctx=" + ctx.toString());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            System.out.println("exceptionCaught() ctx=" + ctx.toString() + ", error=" + cause.getMessage());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
            System.out.println("channelRead() ctx=" + ctx.toString() + ", msg=" + msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
            System.out.println("channelReadComplete() ctx=" + ctx.toString());
        }
    }
}
