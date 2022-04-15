package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

import java.nio.file.Path;

@Data
public class NameServerDirectory implements CloudMessage {

    private String nameServerDir;

    public NameServerDirectory(String nameServerDir) {
        this.nameServerDir = nameServerDir;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.NAME_SERVER_DIRECTORY;
    }
}
