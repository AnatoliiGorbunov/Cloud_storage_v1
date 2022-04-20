package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

import java.nio.file.Path;

@Data
public class FileRequest implements CloudMessage {

    private final Path path;

    public FileRequest(Path path) {
        this.path = path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
