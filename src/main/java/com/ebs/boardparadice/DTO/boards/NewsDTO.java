package com.ebs.boardparadice.DTO.boards;

import com.ebs.boardparadice.model.BoardType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsDTO {

    private int id;

    private String title;

    private String content;

    private String writerId;

    private BoardType typeId;

}
