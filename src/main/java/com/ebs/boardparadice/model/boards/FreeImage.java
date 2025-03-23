package com.ebs.boardparadice.model.boards;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeImage {

    private String fileName;

    // 이미지 순서 (대표 이미지 구분용)
    private int id;
    
    public void setId(int id) {
    	this.id = id;
    }

}