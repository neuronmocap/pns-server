package com.noitom.pnstream.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class DownOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        byte[] data = (byte[])msg;
        ctx.writeAndFlush(Unpooled.copiedBuffer(data), promise);
    }
}
