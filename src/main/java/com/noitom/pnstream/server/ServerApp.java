package com.noitom.pnstream.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ServerApp {
    private final ChannelGroup downChannelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel downServerChannel;
    private Channel upServerChannel;

    public ChannelFuture startDownServer(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("DownOutboundHandler", new DownOutboundHandler());
                        pipeline.addLast("DownInboundHandler", new DownInboundHandler(downChannelGroup));
                    }
                });
        ChannelFuture future = bootstrap.bind(address);
        future.syncUninterruptibly();
        downServerChannel = future.channel();
        return future;
    }

    public ChannelFuture startUpServer(InetSocketAddress address){
        ServerBootstrap upBootstrap = new ServerBootstrap();
        upBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("UpInboundHandler",new UpInboundHandler(downChannelGroup));
                    }
                })
                .localAddress(address);
        ChannelFuture future = upBootstrap.bind(address);
        future.syncUninterruptibly();
        upServerChannel = future.channel();
        return future;
    }

    public void destroyServers(){
        if(downServerChannel != null){
            downServerChannel.close();
        }
        if(upServerChannel != null){
            upServerChannel.close();
        }
        downChannelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception{

        final ServerApp endpoint = new ServerApp();

        ChannelFuture downChannelFuture = endpoint.startDownServer(new InetSocketAddress(10000));
        System.out.println("Down running at port:10000");

        ChannelFuture upChannelFuture = endpoint.startUpServer(new InetSocketAddress(9998));
        System.out.println("UP running at port:9998");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                endpoint.destroyServers();
            }
        });

        downChannelFuture.channel().closeFuture().syncUninterruptibly();
        upChannelFuture.channel().closeFuture().syncUninterruptibly();
    }
}
