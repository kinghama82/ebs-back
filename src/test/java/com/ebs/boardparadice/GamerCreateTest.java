package com.ebs.boardparadice;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.GamerRole;
import com.ebs.boardparadice.repository.GamerRepository;
import jakarta.persistence.criteria.From;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class GamerCreateTest {


    @Autowired
    GamerRepository gamerRepository;

    @Test
    void create(){

        Gamer gamer = new Gamer();

        gamer.setName("원태연");
        gamer.setAge(27);
        gamer.setEmail("78wo78@naver.com");
        gamer.setPassword("dnjsxo123");
        gamer.setNickname("베리아빠");
        gamer.setPhone("01039039107");
        gamer.setAddress("유현로33번길 73 405동 1203호");
        gamer.setRole("ADMIN");
        gamer.setCreatedate(LocalDateTime.now());
        gamer.setLevel("100");
        
        gamerRepository.save(gamer);

    }
}
