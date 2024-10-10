package com.banson.healthtagram.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String , Object> redisTemplate;


    //모든 방을 찾는 메서드
    public Set findAllRoom(String myName) {
        return redisTemplate.opsForSet().members(myName);
    }

    //방 생성 메서드
    public String createRoom(String myName, String fName) {
        String randomId = UUID.randomUUID().toString();

        redisTemplate.opsForSet().add(fName, randomId);
        redisTemplate.opsForSet().add(myName, randomId);
        redisTemplate.opsForSet().add(randomId, myName);

        return randomId;
    }

    public void outRoom(String roomId, String name) {
        redisTemplate.opsForSet().remove(roomId, name);
        redisTemplate.opsForSet().remove(name, roomId);
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try{
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}