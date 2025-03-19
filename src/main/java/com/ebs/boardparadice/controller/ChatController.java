package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OpenAIService openAIService;

    public ChatController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping
    public Mono<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        return openAIService.getChatResponse(message)
                .map(reply -> Map.of("reply", reply));
    }
}