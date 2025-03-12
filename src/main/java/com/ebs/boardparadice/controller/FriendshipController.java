package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.FriendshipDTO;
import com.ebs.boardparadice.DTO.FriendshipRequestDTO;
import com.ebs.boardparadice.model.Friendship;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.service.FriendshipService;
import com.ebs.boardparadice.service.GamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friendship")  // ✅ 프론트와 동일하게 변경
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final GamerService gamerService;

    @GetMapping("/{gamerId}")
    public List<FriendshipDTO> getFriends(@PathVariable(name = "gamerId") Integer gamerId) {
        List<Friendship> friendships = friendshipService.getFriendsByGamerId(gamerId);

        return friendships.stream()
                .map(FriendshipDTO::new)  // DTO 생성자 사용하여 변환
                .collect(Collectors.toList());
    }


   /* @GetMapping("/{gamerId}")
    public ResponseEntity<List<Friendship>> getFriends(@PathVariable Integer gamerId) {
        List<Friendship> friends = friendshipService.getFriendsByGamerId(gamerId);
        if (friends.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(friends);
    }*/


    @PostMapping
    public ResponseEntity<FriendshipDTO> addFriend(@RequestBody FriendshipRequestDTO request) {
        // gamer ID 검증
        Gamer gamer = gamerService.getGamerById(request.getGamerId());
        if (gamer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        // friend ID 검증 및 닉네임 조회
        Gamer friend = gamerService.getGamerById(request.getFriendId());
        if (friend == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Friendship friendship = new Friendship();
        friendship.setGamer(gamer);
        friendship.setFriend(friend);

        Friendship savedFriendship = friendshipService.addFriend(friendship);

        // ✅ 엔티티 기반으로 DTO 생성
        FriendshipDTO responseDTO = new FriendshipDTO(savedFriendship);

        return ResponseEntity.ok(responseDTO);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFriend(@PathVariable(name = "id") Integer id) {
        friendshipService.removeFriend(id);
        return ResponseEntity.noContent().build();
    }

}
