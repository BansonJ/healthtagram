package com.banson.healthtagram.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/cre")
    public String createRoom(@RequestParam(name = "myName") String myName, @RequestParam(name = "fName") String fName) {

        return chatService.createRoom(myName, fName);
    }

    @GetMapping("/get")
    public Set findAllRoom(@RequestParam(name = "myName") String myName) {
        return chatService.findAllRoom(myName);
    }

    @DeleteMapping("/delete")
    public void outRoom(@RequestParam(name = "name") String name, @RequestParam(name = "roomId") String roomId) {
        chatService.outRoom(roomId, name);
    }
}