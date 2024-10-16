package com.banson.healthtagram.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final RedisTemplate<String, Object> redisTemplate;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        /*
        {
            "sender":"정승현",
            "roomId":"77d5fea8-0662-4d49-891d-c1ee748e4806",
            "message":"안녕하시오."
        }
        */

        String payload = message.getPayload();
        log.info("페이로드:{}", payload);

        //payload를 ChatMessage 객체로 만들어주기
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        handlerActions(session, chatMessage);
    }

    public void handlerActions(WebSocketSession session, ChatMessage chatMessage) {
        List members = redisTemplate.opsForSet().members(chatMessage.getRoomId()).stream().toList();

        log.info("list: {}", redisTemplate.opsForSet().members(chatMessage.getRoomId()).stream().toList());

        if (!(members.contains(chatMessage.getSender())) && !(sessions.contains(session))) {    //방에 처음 들어왔을때 or 나갔다 들어왔을 때
            redisTemplate.opsForSet().add(chatMessage.getRoomId(), chatMessage.getSender());
            sessions.add(session);
            sendMessage(chatMessage.getSender() + " 님이 입장했습니다.");
        } else if (members.contains(chatMessage.getSender()) && !(sessions.contains(session))) {    //방에 재입장 했을 때
            sessions.add(session);
            sendMessage(chatMessage.getSender() + ": " + chatMessage.getMessage());
        } else {    //방에 있을 때
            sendMessage(chatMessage.getSender() +": " + chatMessage.getMessage());
        }
    }

    private <T> void sendMessage(T message) {   //채팅방에 입장해 있는 모든 클라이언트에게 메세지 전송
        sessions.parallelStream()
                .forEach(session -> chatService.sendMessage(session, message));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
