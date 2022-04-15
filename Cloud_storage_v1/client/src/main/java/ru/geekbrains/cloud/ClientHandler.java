package ru.geekbrains.cloud;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.messagemodel.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private ClientController clientController;
    private Path serverDir;
    private Path searchDir;


    ClientHandler(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connect!!!");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getMessageType()) {
            case NAME_SERVER_DIRECTORY:
                NameServerDirectory nameSerDir = (NameServerDirectory) cloudMessage;
                serverDir = Paths.get(nameSerDir.getNameServerDir());
                Platform.runLater(() -> {
                    clientController.serverPath.setText(String.valueOf(serverDir.toAbsolutePath()));
                });
                break;
            case LIST:
                ListMessage lm = (ListMessage) cloudMessage;
                Platform.runLater(() -> {
                    clientController.serverView.getItems().clear();
                    clientController.serverView.getItems().add("...");
                    clientController.serverView.getItems().addAll(lm.getFiles());
                });
                break;
            case COMMAND_MESSAGE:
                CommandMessage cm = (CommandMessage) cloudMessage;
                if (cm.getNumberOrder() == 8001) {
                    clientController.setAuthorizedIsOk();
                }
                break;
            case SEARCH_MESSAGE:
                SearchMessage sm = (SearchMessage) cloudMessage;
                searchDir = Paths.get(sm.getSearch());
                Platform.runLater(() -> {
                    clientController.serverPath.setText(String.valueOf(searchDir.toAbsolutePath()));
                });
                break;
        }
    }
}

