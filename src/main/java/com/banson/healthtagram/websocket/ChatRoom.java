package com.banson.healthtagram.websocket;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class ChatRoom {

    private String roomId;
    private String name;
}