package com.noitom.pnstream.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final ChannelGroup group;
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS");
    private static long time1=0;
    private static long count=0;

    public UpInboundHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("a TCP Up client joined:" + ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        long curTime = System.currentTimeMillis();
        if(time1 == 0 || curTime-time1>=10000){ //print every 10 seconds
            time1 = curTime;
            System.out.println(dateFormat.format(new Date(curTime))+",viewers="+group.size()+
                    ",fps="+(count/10)+",size="+msg.readableBytes());

            count = 0;
        }
        count++;

        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        if(group.size() > 0){
            group.writeAndFlush(data);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("UpInboundHandler exceptionCaught:" + ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }
}
