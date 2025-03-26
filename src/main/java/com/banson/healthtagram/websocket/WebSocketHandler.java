package com.banson.healthtagram.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // 각 방의 WebSocket 연결을 관리하는 Map (방 ID -> WebSocketSession)
    private static final Map<String, Map<String, WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocket 세션이 연결되었을 때, 방 ID를 URL에서 추출해서 저장합니다.
        String roomId = getRoomIdFromSession(session);
        roomSessions.putIfAbsent(roomId, new ConcurrentHashMap<>());
        roomSessions.get(roomId).put(session.getId(), session);
        System.out.println("새로운 연결: " + session.getId() + " -> 방: " + roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지를 받은 후, 해당 방에 연결된 모든 클라이언트에게 메시지를 전달합니다.
        String roomId = getRoomIdFromSession(session);
        Map<String, WebSocketSession> sessions = roomSessions.get(roomId);

        if (sessions != null) {
            // 해당 방에 연결된 모든 클라이언트에게 메시지 전송
            for (WebSocketSession webSocketSession : sessions.values()) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(message.getPayload()));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // 연결 종료 시, 해당 방에서 세션을 제거합니다.
        String roomId = getRoomIdFromSession(session);
        Map<String, WebSocketSession> sessions = roomSessions.get(roomId);

        if (sessions != null) {
            sessions.remove(session.getId());
            System.out.println("연결 종료: " + session.getId() + " -> 방: " + roomId);
        }
    }
    
    private String getRoomIdFromSession(WebSocketSession session) {
        // 예: ws://localhost:8080/chat/room1 형태에서 'room1'을 추출
        return session.getUri().getPath().split("/")[2];
    }
}

