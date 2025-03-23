package com.ebs.boardparadice.DTO;

import com.ebs.boardparadice.model.Friendship;
import com.ebs.boardparadice.model.Gamer;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FriendshipDTO {
    private Integer id;
    private Integer gamerId;
    private Integer friendId;
    private String friendNickname;  // 친구 닉네임 추가!
    private String friendImg;       // ✅ 친구의 프로필 이미지 추가!

    public FriendshipDTO(Friendship friendship) {
        this.id = friendship.getId();
        this.gamerId = friendship.getGamer().getId();
        this.friendId = friendship.getFriend().getId();
        this.friendNickname = friendship.getFriend().getNickname();  // 친구 닉네임 설정
        this.friendImg = friendship.getFriend().getProfileImage();  // ✅ 친구의 프로필 이미지 설정
    }
}
