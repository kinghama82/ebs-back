package com.ebs.boardparadice.DTO.boards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameCategoryDTO {
    private int id;
    private String gameCategory; // 카테고리 이름
    private String description;
}
