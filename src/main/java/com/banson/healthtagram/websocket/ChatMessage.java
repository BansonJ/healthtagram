package com.banson.healthtagram.websocket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    //단순 DTO
    private String roomId;
    private String sender;
    private String message;
}