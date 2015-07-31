package com.hula.ihor.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Igor on 30.07.2015.
 */
public class TelnetClientHandler extends SimpleChannelInboundHandler<String> {

    private static double money = 10000;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.println(msg);
        dealWithRoundResult(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void dealWithRoundResult(String result) {
        if (result.contains("lost")) {
            money -= Double.parseDouble(result.split(" ")[2]);
        } else if (result.contains("won")) {
            money += Double.parseDouble(result.split(" ")[2]);
        }
    }
}
