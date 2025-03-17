package com.ebs.boardparadice.service.boards;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.DTO.boards.QuestionDTO;
import com.ebs.boardparadice.model.boards.Question;
import com.ebs.boardparadice.repository.boards.QuestionRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    
    //읽기 + 댓글리스트 포함
    public QuestionDTO getQuestion(int id) {
    	Optional<Question> result = questionRepository.findByIdWithAnswers(id);
    	Question question = result.orElseThrow();
    	
    	QuestionDTO questionDTO = entityToDTO(question);
    	
    	//댓글리스트
    	questionDTO.setAnswerList(
    			question.getAnswerList().stream()
    			.map(answer -> AnswerDTO.builder()
    					.id(answer.getId())
	            		.content(answer.getContent())
	                    .gamer(answer.getGamer())
	                    .createdate(answer.getCreatedate())
	                    .voter(answer.getVoter())
	                    .question(answer.getQuestion().getId())
	                    .build())
	            .collect(Collectors.toList())
	    );
    	return questionDTO;
    }
    
    
    
    //entity -> dto
    public QuestionDTO entityToDTO(Question q) {
 	   QuestionDTO dto = QuestionDTO.builder()
 			   .id(q.getId())
 			   .title(q.getTitle())
 			   .writerId(q.getWriterId())
 			   .content(q.getContent())
 			   .createdate(q.getCreatedate())
 			   .answerList(
 					   q.getAnswerList() != null ?
 					   q.getAnswerList().stream()
 					   .map(answer -> AnswerDTO.builder()
 		                        .id(answer.getId())
 		                        .content(answer.getContent())
 		                        .gamer(answer.getGamer())
 		                        .createdate(answer.getCreatedate())
 		                        .voter(answer.getVoter())
 		                        .question(answer.getQuestion().getId())  // ✅ 자유게시판이므로 `free` 필드 사용
 		                        .build())
 		                    .collect(Collectors.toList())
 		                : new ArrayList<>())  // ✅ `answerList`가 없으면 빈 리스트 반환
 		            .voter(q.getVoter())
 		            .build();
 	   return dto;
    }
}
