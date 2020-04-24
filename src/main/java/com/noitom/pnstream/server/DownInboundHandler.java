package com.noitom.pnstream.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;

public class DownInboundHandler extends ChannelInboundHandlerAdapter {
    private final ChannelGroup group;

    public DownInboundHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("a TCP down client joined:" + ctx.channel());
        group.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("DownInboundHandler exceptionCaught:" + ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }
}
