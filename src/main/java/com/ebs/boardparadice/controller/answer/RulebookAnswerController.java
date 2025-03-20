package com.ebs.boardparadice.controller.answer;

import com.ebs.boardparadice.DTO.answers.RuleAnswerRequest;
import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;

import com.ebs.boardparadice.model.answers.RulebookAnswer;
import com.ebs.boardparadice.service.answers.RulebookAnswerService;
import lombok.RequiredArgsConstructor;
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
    public void deleteAnswer(@PathVariable int rulebookId, @PathVariable int answerId, @RequestParam int userId) {
        answerService.deleteAnswer(answerId, userId);
    }
}
