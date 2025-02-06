package com.ebs.boardparadice.requestDTO;

import com.ebs.boardparadice.model.BoardType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RulebookRequstDTO {

    private int id;
    private String title;
    private String content;
    private String writer_id;
    private BoardType type;


}
