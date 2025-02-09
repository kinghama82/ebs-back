package com.ebs.boardparadice.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponseDTO {
    private int id;
    private String gameName;
    private String year;
    private String players;
    private String time;
    private String reage;
    private String company;
    private String sCompany;
    private int price;
    private String enGameName;
    private String bestPlayers;
    private float avg;
    private int gamerank;
    private String img;

    // ✅ 이미지 URL이 null일 경우 기본값 설정
    public String getImg() {
        return (img != null) ? img : "";
    }
}
