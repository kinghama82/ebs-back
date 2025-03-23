package com.ebs.boardparadice.service.answers;


import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.answers.FreeAnswerDTO;
import com.ebs.boardparadice.model.answers.FreeAnswer;
import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.repository.answers.FreeAnswerRepository;
import com.ebs.boardparadice.repository.boards.FreeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeAnswerService {

	private final FreeAnswerRepository freeAnswerRepository;
	private final FreeRepository freeRepository;
	
	//등록
	public int createFreeAnswer(FreeAnswerDTO freeAnswerDTO) {
		FreeAnswer freeAnswer = DTOtoEntity(freeAnswerDTO);
		
		FreeAnswer saveFreeAnswer = freeAnswerRepository.save(freeAnswer);
		return saveFreeAnswer.getId();
	}
	
	//상세
	public FreeAnswerDTO getFreeAnswer(int id) {
		Optional<FreeAnswer> result = freeAnswerRepository.findById(id);
		FreeAnswer answer = result.orElseThrow();
		FreeAnswerDTO dto = entityToDTO(answer);
		return dto;
	}
	
	//수정
	public void modifyFreeAnswer(FreeAnswerDTO freeAnswerDTO) {
		Optional<FreeAnswer> result = freeAnswerRepository.findById(freeAnswerDTO.getId());
		FreeAnswer freeAnswer = result.orElseThrow();
		
		freeAnswer.setContent(freeAnswerDTO.getContent());
		
		freeAnswerRepository.save(freeAnswer);
		
	}
	//삭제
	public void deleteFreeAnswer(int id) {
		freeAnswerRepository.deleteById(id);
	}
	
	//entity -> dto
	private FreeAnswerDTO entityToDTO(FreeAnswer freeAnswer) {
		FreeAnswerDTO dto = FreeAnswerDTO.builder()
							.id(freeAnswer.getId())
							.content(freeAnswer.getContent())
							.gamer(freeAnswer.getGamer())
							.free(freeAnswer.getFree().getId())
							.createdate(freeAnswer.getCreatedate())
							.build();
		return dto;							
	}
	//dto -> entity
	private FreeAnswer DTOtoEntity(FreeAnswerDTO dto) {
		Free free = freeRepository.findById(dto.getFree())
					.orElseThrow(() -> new IllegalArgumentException("해당 자유글이 없습니다."));
		
		
		FreeAnswer answer = FreeAnswer.builder()
							.content(dto.getContent())
							.gamer(dto.getGamer())
							.free(free)
							.createdate(LocalDateTime.now())
							.build();
		return answer;							
	}



}
