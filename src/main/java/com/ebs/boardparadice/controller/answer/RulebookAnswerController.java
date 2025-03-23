package com.ebs.boardparadice.controller.answer;

import com.ebs.boardparadice.DTO.answers.RuleAnswerRequest;
import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;

import com.ebs.boardparadice.model.answers.RulebookAnswer;
import com.ebs.boardparadice.service.answers.RulebookAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rulebook/{rulebookId}/answers")
@RequiredArgsConstructor
public class RulebookAnswerController {
    private final RulebookAnswerService answerService;

    @GetMapping
    public List<RulebookAnswerDTO> getAnswers(@PathVariable int rulebookId) {
        return answerService.getAnswersByRulebookId(rulebookId);
    }

    @PostMapping("/create")
    public List<RulebookAnswerDTO> addAnswer(@PathVariable int rulebookId, @RequestBody RuleAnswerRequest request) {
        answerService.addAnswer(rulebookId, request.getWriterId(), request.getContent());
        List<RulebookAnswerDTO> answerDto = answerService.getAnswersByRulebookId(rulebookId);

        return answerDto;
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<String> deleteAnswer(@PathVariable Integer answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.ok("✅ 답글이 삭제되었습니다.");
    }
}
