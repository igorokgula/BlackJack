package com.hula.ihor.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by Igor on 30.07.2015.
 */
public final class TelnetClient {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8992" : "8023"));

    private static final String HIT = "hit";
    private static final String STAND = "stand";
    private static final String DOUBLE = "double";

    private static final int MIN_BET = 100;
    private static final int MAX_BET = 1000;

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer());

            Channel ch = b.connect(HOST, PORT).sync().channel();

            while (true) {
                Scanner scanner = new Scanner(System.in);
                Double bet = null;
                while (true) {
                    try {
                        bet = scanner.nextDouble();
                        if (bet > MIN_BET && bet < MAX_BET) {
                            break;
                        }
                        throw new InputMismatchException();
                    } catch (InputMismatchException e) {
                        System.out.println("Not correct!\r\n");
                        scanner.nextLine();
                    }
                }
                ch.writeAndFlush(bet + "\r\n");

                while (true) {
                    String action = scanner.nextLine();
                    if (action.toLowerCase().equals(STAND)
                            || action.toLowerCase().equals(DOUBLE)) {
                        ch.writeAndFlush(action);
                        break;
                    } else if (action.toLowerCase().equals(HIT)) {
                        ch.writeAndFlush(action);
                    }
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}