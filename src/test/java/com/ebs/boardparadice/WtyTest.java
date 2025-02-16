package com.ebs.boardparadice;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.BoardTypeRepository;
import com.ebs.boardparadice.repository.boards.RulebookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class WtyTest {

    @Autowired
    GamerRepository gamerRepository;

    @Autowired
    BoardTypeRepository boardTypeRepository;

    @Autowired
    RulebookRepository rulebookRepository;

    @Test
    void create() {
        // 1. Gamer 객체 30개 생성 및 저장
        for (int i = 1; i <= 30; i++) {
            Gamer gamer = new Gamer();
            gamer.setName("Gamer " + i);
            gamer.setAge(20 + i);
            gamer.setEmail("gamer" + i + "@mail.com");
            gamer.setPassword("password" + i);
            gamer.setNickname("nickname" + i);
            gamer.setPhone("010-1234-567" + i);
            gamer.setAddress("Address " + i);
            gamer.setRole("USER");
            gamer.setCreatedate(LocalDateTime.now());
            gamer.setLevel("10");

            gamerRepository.save(gamer); // Gamer 객체 저장
        }

        // 2. BoardType 객체 하나 생성 및 저장
        BoardType boardType = new BoardType();
        boardType.setBoardname("Strategy");
        boardTypeRepository.save(boardType); // BoardType 객체 저장

        // 3. Rulebook 객체 30개 생성 및 저장
        for (int i = 1; i <= 30; i++) {
            Rulebook rulebook = new Rulebook();
            rulebook.setTitle("Rulebook Title " + i);
            rulebook.setContent("Content for rulebook " + i);

            // 작성자 설정 (Gamer 객체 중 하나)
            Gamer writer = gamerRepository.findById(i).orElse(null); // gamerRepository에서 해당 Gamer 가져오기
            rulebook.setWriterId(writer);

            // 참여자 설정 (Gamer 객체 중 일부)
            Set<Gamer> voters = new HashSet<>();
            for (int j = 1; j <= 5; j++) {
                Gamer voter = gamerRepository.findById(j).orElse(null); // 여러 명의 Gamer 가져오기
                if (voter != null) {
                    voters.add(voter);
                }
            }
            rulebook.setVoter(voters);

            // BoardType 설정
            rulebook.setType(boardType);

            rulebookRepository.save(rulebook); // Rulebook 객체 저장
        }
    }
}
