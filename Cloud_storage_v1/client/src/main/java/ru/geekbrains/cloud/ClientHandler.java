package ru.geekbrains.cloud;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.messagemodel.CloudMessage;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
    log.debug("Client connect");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CloudMessage cloudMessage) throws Exception {

    }


}
