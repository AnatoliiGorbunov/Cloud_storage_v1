package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

@Data
public class CommandMessage implements CloudMessage{

    private int numberOrder;

    public CommandMessage(int numberOrder) {
        this.numberOrder = numberOrder;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COMMAND_MESSAGE;
    }
}
