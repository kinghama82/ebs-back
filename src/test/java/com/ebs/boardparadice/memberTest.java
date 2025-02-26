package com.ebs.boardparadice;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.GamerRole;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.service.GamerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class memberTest {

    @Autowired
    private GamerRepository gamerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GamerService gamerService;

    @Test
    public void testRegisterGamer_withAdminNickname_shouldAddAdminRole() {
        // given: 필수 정보가 채워진 Gamer 객체 생성
        Gamer gamer = new Gamer();
        gamer.setName("김정호");
        gamer.setAge(30);
        gamer.setEmail("user2@aaa.com");
        gamer.setPassword("1111");
        gamer.setNickname("테스트1");
        gamer.setPhone("010-1234-5679");
        gamer.setAddress("Seoul");
        gamer.setSocial(false);
        // level은 필수 값으로 기본 값 설정
        gamer.setLevel("10");

        // when: 회원가입 실행
        Gamer registeredGamer = gamerRepository.save(gamer);

    }
}
