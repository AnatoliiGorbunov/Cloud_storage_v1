package ru.geekbrains.cloud;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.geekbrains.cloud.messagemodel.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\Server\\Server_cloud");
        ctx.writeAndFlush(new ListMessage(serverDir));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case AUTH_MESSAGE:
                AuthMessage am = (AuthMessage) cloudMessage;
                if (AuthService.isAuth(am)) {
                    ctx.writeAndFlush(new AuthMessage(am.getLogin(), am.getPassword()));
                    break;
                }
                break;
            case FILE:
                FileMessage fm = (FileMessage) cloudMessage;
                Files.write(serverDir.resolve(fm.getName()), fm.getBytes());
                ctx.writeAndFlush(new ListMessage(serverDir));
                break;
            case FILE_REQUEST:
                FileRequest fr = (FileRequest) cloudMessage;
                ctx.writeAndFlush(new FileMessage(serverDir.resolve(fr.getName())));
                break;
        }
    }
}
