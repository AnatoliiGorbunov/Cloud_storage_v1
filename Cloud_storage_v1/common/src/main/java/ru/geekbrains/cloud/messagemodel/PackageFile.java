package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

import java.nio.file.Path;

@Data
public class PackageFile implements CloudMessage{

    private String fileName;
    private boolean lastPackage;
    private byte[] dataPackage;


    public PackageFile(Path path, boolean lastPackage, byte[] dataPackage) {
        this.fileName = path.getFileName().toString();
        this.lastPackage = lastPackage;
        this.dataPackage = dataPackage;
    }
    @Override
    public MessageType getMessageType() {
        return MessageType.PACKAGE_FILE;
    }
}
