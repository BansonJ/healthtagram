package com.banson.healthtagram.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChatMessageListener {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void onMessage(String message, String channel) {
        String roomId = channel.split("_")[2];
        applicationEventPublisher.publishEvent(new ChatMessageEvent(this, roomId, message));
    }
}
