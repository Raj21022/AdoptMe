package com.adoptme.controller;

import com.adoptme.dto.ChatInboxItemDto;
import com.adoptme.dto.ChatMessageDto;
import com.adoptme.security.UserPrincipal;
import com.adoptme.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/send")
    public void sendMessage(@Payload Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = payload.get("content").toString();
        
        ChatMessageDto message = chatService.saveMessage(senderId, receiverId, content);
        
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                message
        );

        // Echo message to sender so chat updates instantly on sender side as well.
        messagingTemplate.convertAndSendToUser(
                senderId.toString(),
                "/queue/messages",
                message
        );

        String conversationTopic = "/topic/chat/" + getConversationKey(senderId, receiverId);
        messagingTemplate.convertAndSend(conversationTopic, message);
    }
    
    @GetMapping("/conversation")
    public ResponseEntity<List<ChatMessageDto>> getConversation(
            @RequestParam Long user1,
            @RequestParam Long user2) {
        List<ChatMessageDto> messages = chatService.getConversation(user1, user2);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<ChatInboxItemDto>> getInbox(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ChatInboxItemDto> inbox = chatService.getInbox(currentUser.getId());
        return ResponseEntity.ok(inbox);
    }

    private String getConversationKey(Long user1, Long user2) {
        long a = Math.min(user1, user2);
        long b = Math.max(user1, user2);
        return a + "_" + b;
    }
}
