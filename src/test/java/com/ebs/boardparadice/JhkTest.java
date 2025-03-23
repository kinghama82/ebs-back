package com.ebs.boardparadice;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.repository.BoardTypeRepository;
import com.ebs.boardparadice.service.GamerService;
import com.ebs.boardparadice.service.boards.FreeService;

@SpringBootTest
public class JhkTest {

	@Autowired
	private FreeService freeService;

	@Autowired
	private GamerService gamerService;

	@Autowired
	private BoardTypeRepository boardTypeRepository;

	@Test
	void jhkTest() {

		for (int i = 0; i < 100; i++) {
			
			  FreeDTO dto = new FreeDTO();
			  
			  dto.setContent("테스트 내용 : " + i); 
			  dto.setTitle("테스트 제목 : " + i);
			  dto.setWriterId(gamerService.getGamerById(1));
			  dto.setCreatedate(LocalDate.now());
			  
			 freeService.createFree(dto);
			 
			// freeService.deleteFree(i);

		}
	}
}
