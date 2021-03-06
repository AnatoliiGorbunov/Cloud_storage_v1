package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

@Data
public class AuthMessage implements CloudMessage{

    private final String login;
    private final String password;

    public AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.AUTH_MESSAGE;
    }
}
