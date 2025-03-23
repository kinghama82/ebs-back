package com.ebs.boardparadice.DTO.boards;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// GameRequestDTO.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private int id;
    private String gameName;
    private String year;
    private String players;
    private String time;
    private String reage;
    private String company;
    private String sCompany;
    private Integer price;
    private String enGameName;
    private String bestPlayers;
    private Float avg;
    private Integer gamerank;
    private String img;
    private Set<GameCategoryDTO> gameCategory;

    // ✅ 이미지 URL이 null일 경우 기본값 설정
    public String getImg() {
        return (img != null) ? img : "";
    }
}
