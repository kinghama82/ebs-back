package com.ebs.boardparadice.responseDTO;

import com.ebs.boardparadice.model.BoardType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RulebookResponseDTP {

    private int id;
    private String title;
    private String content;
    private String writer_id;
    private BoardType type;

}
