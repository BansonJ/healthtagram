package com.banson.healthtagram.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 채팅방에 메시지를 저장
    public void saveMessage(String roomId, String message) {
        redisTemplate.opsForList().leftPush("chat_room:" + roomId, message);
    }

    // 채팅방 메시지 불러오기
    public List<String> getMessages(String roomId) {
        return redisTemplate.opsForList().range("chat_room:" + roomId, 0, -1);
    }
}