package com.banson.healthtagram.websocket;

import org.springframework.context.ApplicationEvent;

public class ChatMessageEvent extends ApplicationEvent {

    private final String roomId;
    private final String message;

    public ChatMessageEvent(Object source, String roomId, String message) {
        super(source);
        this.roomId = roomId;
        this.message = message;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getMessage() {
        return message;
    }
}