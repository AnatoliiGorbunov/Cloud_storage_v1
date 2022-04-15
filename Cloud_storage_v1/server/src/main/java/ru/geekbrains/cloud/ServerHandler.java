package ru.geekbrains.cloud;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.messagemodel.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\server\\Server_cloud");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\server\\Server_cloud");
        ctx.writeAndFlush(new NameServerDirectory(serverDir.toString()));
        ctx.writeAndFlush(new ListMessage(serverDir));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case AUTH_MESSAGE:
                AuthMessage am = (AuthMessage) cloudMessage;
                while (true) {
                    if (AuthService.isAuth(am)) {
                        CommandMessage cm = new CommandMessage(Settings.AUTHORIZATION_PASSED);
                        ctx.writeAndFlush(cm);
                        break;
                    }
                    if (!AuthService.isAuth(am)) {
                        break;
                    }
                }
                break;
            case SEARCH_MESSAGE:
                SearchMessage sm = (SearchMessage) cloudMessage;
                final String fileToFind = sm.getSearch();
                serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\server\\Server_cloud");
                try {
                    Files.walkFileTree(serverDir, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            String fileString = file.toAbsolutePath().toString();
                            if (fileString.endsWith(fileToFind)) {
                                String search = String.valueOf(file.toAbsolutePath().getParent());
                                serverDir = Paths.get(search);
                                ctx.writeAndFlush(new SearchMessage(search));
                                ctx.writeAndFlush(new ListMessage(serverDir));
                                System.out.println("file found at path: " + file.toAbsolutePath());
                                return FileVisitResult.TERMINATE;

                            }
                            return FileVisitResult.CONTINUE;

                        }

                    });
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
