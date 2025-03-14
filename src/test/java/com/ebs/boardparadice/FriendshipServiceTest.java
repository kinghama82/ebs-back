package com.ebs.boardparadice;

import com.ebs.boardparadice.model.Friendship;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.repository.FriendshipRepository;
import com.ebs.boardparadice.service.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Commit // 테스트 후 데이터 유지
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private Gamer gamer;
    private Gamer friend;
    private Friendship friendship;

    @BeforeEach
    void setUp() {
        gamer = new Gamer();
        gamer.setId(1);
        gamer.setName("테스트 유저");

        friend = new Gamer();
        friend.setId(2);
        friend.setName("친구 유저");

        friendship = new Friendship();
        friendship.setId(1);
        friendship.setGamer(gamer);
        friendship.setFriend(friend);
    }

    @Test
    void testGetFriendsByGamerId() {
        when(friendshipRepository.findByGamerId(1)).thenReturn(Arrays.asList(friendship));

        List<Friendship> friends = friendshipService.getFriendsByGamerId(1);

        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals("친구 유저", friends.get(0).getFriend().getName());

        verify(friendshipRepository, times(1)).findByGamerId(1);
    }

    @Test
    void testAddFriend() {
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship savedFriendship = friendshipService.addFriend(friendship);

        assertNotNull(savedFriendship);
        assertEquals(1, savedFriendship.getId());
        assertEquals("테스트 유저", savedFriendship.getGamer().getName());
        assertEquals("친구 유저", savedFriendship.getFriend().getName());

        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testRemoveFriend() {
        doNothing().when(friendshipRepository).deleteById(1);

        friendshipService.removeFriend(1);

        verify(friendshipRepository, times(1)).deleteById(1);
    }
}
