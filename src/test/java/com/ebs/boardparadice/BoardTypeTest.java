package com.ebs.boardparadice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.repository.BoardTypeRepository;

@SpringBootTest
public class BoardTypeTest {
	
	@Autowired
	private BoardTypeRepository boardTypeRepository;
	
	@Test
	void typeTest() {
		
		//자기 담당 보드 타입만 주석 해제하고 테스트 1번 돌리면 됩니다
		
		BoardType boardType = new BoardType();
		boardType.setBoardname("Free");
		//boardType.setBoardname("News");
		//boardType.setBoardname("Question");
		//boardType.setBoardname("Rulebook");
		//boardType.setBoardname("Game");
		
		boardTypeRepository.save(boardType);
		
		
		
	}

}
