package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.service.answers.AnswerService;
import com.ebs.boardparadice.service.boards.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final FreeService freeService;
    private final QuestionService questionService;
    private final NewsService newsService;
    private final HistoryService historyService;
    private final RulebookService rulebookService;
    private final AnswerService answerService;  // ✅ AnswerService 추가

    // ✅ 특정 사용자가 작성한 게시글 목록 조회
    @GetMapping("/user/{gamerId}")
    public ResponseEntity<Map<String, List<?>>> getUserPosts(@PathVariable(name = "gamerId") int gamerId) {
        Map<String, List<?>> userPosts = Map.of(
                "free", freeService.getPostsByGamerId(gamerId)
        /*        "question", questionService.getPostsByGamerId(gamerId),
                "news", newsService.getPostsByGamerId(gamerId),
                "history", historyService.getPostsByGamerId(gamerId),
                "rulebook", rulebookService.getPostsByGamerId(gamerId) */
        );

        return ResponseEntity.ok(userPosts);
    }

    // ✅ 특정 사용자가 작성한 댓글 목록 조회
    @GetMapping("/user/{gamerId}/comments")
    public ResponseEntity<Map<String, List<AnswerDTO>>> getUserComments(@PathVariable(name = "gamerId") int gamerId) {
        Map<String, List<AnswerDTO>> userComments = answerService.getCommentsByGamerId(gamerId);
        return ResponseEntity.ok(userComments);
    }
}