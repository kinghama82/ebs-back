package com.ebs.boardparadice.controller.answer;

import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;
import com.ebs.boardparadice.service.answers.RulebookAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rulebook/{rulebookId}/answers")
public class RulebookAnswerController {

    private final RulebookAnswerService answerService;

    /**
     * 특정 게시글의 답글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<RulebookAnswerDTO>> getAnswers(@PathVariable int rulebookId) {
        List<RulebookAnswerDTO> answers = answerService.getAnswersByRulebookId(rulebookId);
        return ResponseEntity.ok(answers);
    }

    /**
     * 답글 추가
     */
    @PostMapping("/create")
    public ResponseEntity<RulebookAnswerDTO> addAnswer(
            @PathVariable int rulebookId,
            @RequestBody Map<String, String> requestBody) {

        int writerId = Integer.parseInt(requestBody.get("writerId"));
        String content = requestBody.get("content");

        RulebookAnswerDTO createdAnswer = answerService.addAnswer(rulebookId, writerId, content);
        return ResponseEntity.ok(createdAnswer);
    }

    /**
     * 답글 삭제
     */
    @DeleteMapping("/{answerId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable int answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.ok("답글이 삭제되었습니다.");
    }
}
