package com.ebs.boardparadice.service;

import com.ebs.boardparadice.model.Friendship;
import com.ebs.boardparadice.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public List<Friendship> getFriendsByGamerId(Integer gamerId) {
        return friendshipRepository.findByGamerId(gamerId);
    }

    public Friendship addFriend(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    public void removeFriend(Integer id) {
        friendshipRepository.deleteById(id);
    }
}
