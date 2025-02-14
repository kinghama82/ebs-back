package com.ebs.boardparadice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ebs.boardparadice.repository.boards.RulebookRepository;

@SpringBootTest
class BoardparadiceApplicationTests {


	@Autowired
	RulebookRepository rulebookRepository;



	@Test
	void contextLoads() {

	/*	for (int i = 0; i < 30; i++){

			String title = String.format("테스트데이터 제목입니다[%03d]", i);
			String content = String.format("테스트데이터 내용입니다[%03d]", i);


			Rulebook rulebook = new Rulebook();
			rulebook.setTitle(title);
			rulebook.setContent(content);
			rulebook.setWriterId();

			rulebookRepository.save(rulebook);

		}
*/
		}

	}
