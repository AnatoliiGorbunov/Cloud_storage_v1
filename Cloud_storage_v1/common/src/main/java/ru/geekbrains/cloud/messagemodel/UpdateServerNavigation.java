package ru.geekbrains.cloud.messagemodel;


import lombok.Data;

import java.nio.file.Path;

@Data
public class UpdateServerNavigation implements CloudMessage {

    private Path path;

    public UpdateServerNavigation(Path path) {
        this.path = path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UPDATE_SERVER_NAVIGATION;
    }
}
