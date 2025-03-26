package com.banson.healthtagram.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageListener chatMessageListener;

    // REST API로 채팅 메시지 전송
    @PostMapping("/{roomId}/send")
    public String sendMessage(@PathVariable String roomId, @RequestBody String message) {
        // 메시지를 Redis에 저장
        chatRoomService.saveMessage(roomId, message);

        // 메시지를 Redis 채널을 통해 퍼블리시 (WebSocket으로 메시지 전송)
        chatMessageListener.onMessage(message, "chat_room_" + roomId);

        return "메시지가 전송되었습니다.";
    }

    // 채팅방 메시지 조회
    @GetMapping("/{roomId}/messages")
    public List<String> getMessages(@PathVariable String roomId) {
        return chatRoomService.getMessages(roomId);
    }
}
