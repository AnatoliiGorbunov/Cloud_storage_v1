package ru.geekbrains.cloud;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;


@Slf4j
public class Server {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//Экзекютор серыисы как только их запустим (НиоЭвентЛукГрупп)
        // они будут крутиться в бесконечном цикле
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {//настройка сервера
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)//передаем воркеров
                    .channel(NioServerSocketChannel.class)//тип канала которым будет обслуживаться
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(//обработчики событий которые обрабатывают сообщения между клиентом и сервером//паттерн (цкпочка ответственности)
                                    new ObjectEncoder(),//конвеер обработчиков
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ServerHandler()
                            );

                        }
                    });

            ChannelFuture future = bootstrap.bind(8189).sync();//sync возвращает некоторое будущее (не блокирующая операция)//слушаем порт 8189
            log.debug("Server started...");
            future.channel().closeFuture().sync();//Блокирующая операция//сервер застыл в ожидании событий
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully(); //когда произожло какое то исключение закрываем воркеры
            workerGroup.shutdownGracefully();
        }
    }
}
