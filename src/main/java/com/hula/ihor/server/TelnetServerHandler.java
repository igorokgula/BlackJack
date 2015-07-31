package com.hula.ihor.server;

import com.hula.ihor.domain.Table;
import com.hula.ihor.domain.User;
import com.hula.ihor.exception.TableIsFullException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Igor on 30.07.2015.
 */
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    private User user;

    public TelnetServerHandler(User user) {
        this.user = user;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        user.setChannel(ctx.channel());
        try {
            Table.getInstance().addUser(user);
            ctx.writeAndFlush("Hello!!! \r\n");
        } catch (TableIsFullException e) {
            ctx.writeAndFlush("Sorry, but the table is full!\r\n");
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Table.getInstance().removeUser(user);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        StringBuffer response = new StringBuffer();
        if (user.getBet() == null) {
            user.setBet(Double.parseDouble(request));
        } else if (!user.isMoveDone()) {
            response.append(Table.getInstance().dealWithUserAction(request, user));
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}