package com.ebs.boardparadice.controller.answer;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.service.answers.AnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/{boardType}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
        
    //저장
    @PostMapping("/")
    public ResponseEntity<?> create(
            @PathVariable(name = "boardType") String boardType,
            @RequestBody AnswerDTO answerDTO) {
        try {
            AnswerDTO savedAnswer = answerService.saveAnswer(boardType, answerDTO);
            return ResponseEntity.ok(Map.of("result", "등록 성공", "answer", savedAnswer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("result", "등록 실패", "error", e.getMessage()));
        }
    }
    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable(name = "boardType")String boardType,
            @PathVariable(name = "id")int id){
        answerService.deleteAnswer(boardType, id);
        return ResponseEntity.ok(Map.of("result", "삭제 성공"));
    }
//    //조회
//    @GetMapping("/{id}")
//    public ResponseEntity<AnswerDTO> getAnswer(
//            @PathVariable(name = "boardType") String boardType,
//            @PathVariable(name = "id") int id) {
//        AnswerDTO answerDTO = answerService.getAnswer(id, boardType);
//        return ResponseEntity.status(HttpStatus.OK).body(answerDTO);
//    }
}