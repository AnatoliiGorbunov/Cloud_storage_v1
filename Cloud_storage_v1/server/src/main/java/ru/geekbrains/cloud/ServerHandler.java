package ru.geekbrains.cloud;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.messagemodel.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path serverDir = Paths.get("D:\\WorkingMaterials\\Java.cloud_storage\\Cloud_storage_v1\\Cloud_storage_v1\\server\\Server_cloud");
    private final static int MB_8 = 1024 * 1024 * 8;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new NameServerDirectory(serverDir.toString()));
        ctx.writeAndFlush(new ListMessage(serverDir));

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case UPDATE_SERVER_NAVIGATION:
                UpdateServerNavigation usv = (UpdateServerNavigation) cloudMessage;
                serverDir = Paths.get(String.valueOf(usv.getPath().toAbsolutePath()));
                ctx.writeAndFlush(new ListMessage(serverDir));
                break;
            case FILE_REQUEST:
                FileRequest fr = (FileRequest) cloudMessage;
                Path path = Paths.get(String.valueOf(fr.getPath().toAbsolutePath()));
                int marker = 0;
                try {
                    byte[] data = Files.readAllBytes(Paths.get(String.valueOf(path)));
                    byte[] dataTemp = new byte[MB_8];
                    if (data.length < MB_8) {
                        dataTemp = new byte[data.length];
                    }
                    for (int i = 0; i < data.length; i++) {
                        dataTemp[marker] = data[i];
                        marker++;
                        if (marker == dataTemp.length) {
                            ctx.writeAndFlush(new PackageFile(Paths.get(String.valueOf(path)), false, dataTemp));
                            marker = 0;
                            if (data.length - i < MB_8) {
                                dataTemp = new byte[data.length - i];

                            } else {
                                dataTemp = new byte[MB_8];

                            }
                        }
                    }
                    ctx.writeAndFlush(new PackageFile(Paths.get(String.valueOf(path)), true, dataTemp));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case LIST:
                ListMessage lm = (ListMessage) cloudMessage;
                ctx.writeAndFlush(new ListMessage(serverDir));
                break;
            case PACKAGE_FILE:
                PackageFile pf = (PackageFile) cloudMessage;
                try (FileOutputStream fos = new FileOutputStream(serverDir + File.separator + pf.getFileName(), true);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    bos.write(pf.getDataPackage(), 0, pf.getDataPackage().length);
                    ctx.writeAndFlush(new ListMessage(serverDir));
                } catch (IOException e) {
                    e.printStackTrace();

                }
                if (!pf.isLastPackage()) {
                    ctx.writeAndFlush(new ListMessage(serverDir));
                }
                break;
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
