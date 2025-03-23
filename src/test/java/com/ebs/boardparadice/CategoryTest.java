package com.ebs.boardparadice;

import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.model.boards.GameCategory;
import com.ebs.boardparadice.repository.boards.GameCategoryRepository;
import com.ebs.boardparadice.repository.boards.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CategoryTest {

    @Autowired
    private GameCategoryRepository categoryRepository;

    @Autowired
    private GameRepository gameRepository;

    /*@Test
    public void testCategoryadd() {
        Game game = new Game();
        GameCategory category = new GameCategory();
        category = categoryRepository.findById(1).get();
        game = gameRepository.findById(1).get();

        game.getGameCategory().add(category);
        gameRepository.save(game);
    }*/

    @Test
    @Transactional  // 테스트 실행 시 트랜잭션을 유지하여 lazy 로딩 문제 해결
    @Rollback(false)
    public void testCategoryadd() {
        Game game = gameRepository.findById(1).orElseThrow();
        GameCategory category = categoryRepository.findById(1).orElseThrow();

        game.getGameCategory().add(category);
        gameRepository.save(game);
    }
}
