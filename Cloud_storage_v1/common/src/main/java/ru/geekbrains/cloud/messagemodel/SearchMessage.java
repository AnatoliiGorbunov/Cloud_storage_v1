package ru.geekbrains.cloud.messagemodel;

import lombok.Data;

@Data
public class SearchMessage implements CloudMessage{

    private final String search;

    public SearchMessage(String search) {
        this.search = search;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SEARCH_MESSAGE;
    }
}


