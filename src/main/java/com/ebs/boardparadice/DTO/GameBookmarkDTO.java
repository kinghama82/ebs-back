package com.ebs.boardparadice.DTO;

import com.ebs.boardparadice.model.GameBookmark;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameBookmarkDTO {
    private Integer id;
    private Integer gamerId;
    private Integer gameId;
    private String gameName; // ✅ 게임 이름 추가
    private String gameImg;  // ✅ 게임 이미지 추가

    public GameBookmarkDTO(GameBookmark gameBookmark) {
        this.id = gameBookmark.getId();
        this.gamerId = gameBookmark.getGamer().getId();
        this.gameId = gameBookmark.getGame().getId();
        this.gameName = gameBookmark.getGame().getGameName(); // ✅ 게임 이름 가져오기
        this.gameImg = gameBookmark.getGame().getImg();       // ✅ 게임 이미지 가져오기
    }
}
