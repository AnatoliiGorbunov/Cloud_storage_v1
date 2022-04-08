package ru.geekbrains.cloud.messagemodel;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    MessageType getMessageType();
}
