package com.ebs.boardparadice.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;

    public OpenAIService(WebClient.Builder webClientBuilder,
                         @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> getChatResponse(String prompt) {
        // OpenAI ChatCompletion API 요청을 위한 데이터 구성
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-2024-08-06",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt))
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    // response 데이터에서 답변 추출 (실제 응답 구조에 맞춰 조정)
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    }
                    return "";
                });
    }
}
