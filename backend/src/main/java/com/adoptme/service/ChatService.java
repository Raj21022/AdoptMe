package com.adoptme.service;

import com.adoptme.dto.ChatInboxItemDto;
import com.adoptme.dto.ChatMessageDto;
import com.adoptme.entity.ChatMessage;
import com.adoptme.entity.User;
import com.adoptme.exception.CustomException;
import com.adoptme.repository.ChatMessageRepository;
import com.adoptme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ChatMessageDto saveMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException("Receiver not found"));
        
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        return convertToDto(savedMessage);
    }
    
    public List<ChatMessageDto> getConversation(Long user1Id, Long user2Id) {
        return chatMessageRepository.findConversation(user1Id, user2Id).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ChatInboxItemDto> getInbox(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findMessagesForUserOrderByTimestampDesc(userId);
        Map<Long, ChatInboxItemDto> latestByOtherUser = new LinkedHashMap<>();

        for (ChatMessage message : messages) {
            User otherUser = message.getSender().getId().equals(userId)
                    ? message.getReceiver()
                    : message.getSender();

            if (!latestByOtherUser.containsKey(otherUser.getId())) {
                latestByOtherUser.put(
                        otherUser.getId(),
                        new ChatInboxItemDto(
                                otherUser.getId(),
                                otherUser.getName(),
                                message.getContent(),
                                message.getTimestamp()
                        )
                );
            }
        }

        return new ArrayList<>(latestByOtherUser.values());
    }
    
    private ChatMessageDto convertToDto(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getReceiver().getId(),
                message.getReceiver().getName(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
