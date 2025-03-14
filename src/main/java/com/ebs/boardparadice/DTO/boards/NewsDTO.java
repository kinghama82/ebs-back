package com.ebs.boardparadice.DTO.boards;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor  // ✅ 기본 생성자 추가
@AllArgsConstructor // ✅ 모든 필드를 포함하는 생성자 추가
public class NewsDTO {
    private int id;
    private String title;
    private String content;
    private int writerId;
    private Set<Integer> voterIds = new HashSet<>(); // ✅ 기본값 추가
    private LocalDate createdate;
    private int typeId;
    private String youtubeUrl;
    private List<String> imageUrls;
}
