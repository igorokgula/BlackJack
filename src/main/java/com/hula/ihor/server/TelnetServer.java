package com.hula.ihor.server;

import com.hula.ihor.domain.Table;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Igor on 30.07.2015.
 */
public final class TelnetServer {

    private static final int TIME_BETWEEN_ROUNDS = 15000;

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8992" : "8023"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new TelnetServerInitializer());

            b.childGroup().execute(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(TIME_BETWEEN_ROUNDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Table.getInstance().playRound();
                    }
                }
            });

            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}