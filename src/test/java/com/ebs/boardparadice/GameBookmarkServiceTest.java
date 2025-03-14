package com.ebs.boardparadice;

import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.model.GameBookmark;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.repository.GameBookmarkRepository;
import com.ebs.boardparadice.service.GameBookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameBookmarkServiceTest {

    @Mock
    private GameBookmarkRepository gameBookmarkRepository;

    @InjectMocks
    private GameBookmarkService gameBookmarkService;

    private Gamer gamer;
    private Game game;
    private GameBookmark gameBookmark;

    @BeforeEach
    void setUp() {
        gamer = new Gamer();
        gamer.setId(1);
        gamer.setName("테스트 유저");

        game = new Game();
        game.setId(100);
        game.setGameName("보드게임 테스트");

        gameBookmark = new GameBookmark();
        gameBookmark.setId(1);
        gameBookmark.setGamer(gamer);
        gameBookmark.setGame(game);
    }

    @Test
    void testGetBookmarksByGamerId() {
        when(gameBookmarkRepository.findByGamerId(1)).thenReturn(Arrays.asList(gameBookmark));

        List<GameBookmark> bookmarks = gameBookmarkService.getBookmarksByGamerId(1);

        assertNotNull(bookmarks);
        assertEquals(1, bookmarks.size());
        assertEquals("보드게임 테스트", bookmarks.get(0).getGame().getGameName());

        verify(gameBookmarkRepository, times(1)).findByGamerId(1);
    }

    @Test
    void testAddBookmark() {
        when(gameBookmarkRepository.save(any(GameBookmark.class))).thenReturn(gameBookmark);

        GameBookmark savedBookmark = gameBookmarkService.addBookmark(gameBookmark);

        assertNotNull(savedBookmark);
        assertEquals(1, savedBookmark.getId());
        assertEquals("보드게임 테스트", savedBookmark.getGame().getGameName());

        verify(gameBookmarkRepository, times(1)).save(gameBookmark);
    }

    @Test
    void testRemoveBookmark() {
        doNothing().when(gameBookmarkRepository).deleteById(1);

        gameBookmarkService.removeBookmark(1);

        verify(gameBookmarkRepository, times(1)).deleteById(1);
    }
}
