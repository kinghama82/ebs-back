package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;

import com.ebs.boardparadice.model.boards.Rulebook;

import com.ebs.boardparadice.repository.boards.RulebookRepository;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RulebookService {

    private final RulebookRepository rulebookRepository;
    private final ModelMapper modelMapper;

    //리스트
    public PageResponseDTO<RulebookDTO> getList(PageRequestDTO pageRequestDTO){

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("id").descending());

        Page<Rulebook> result = rulebookRepository.findAll(pageable);

        List<RulebookDTO> dtoList = result.getContent().stream()
                .map(rulebook -> modelMapper.map(rulebook, RulebookDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<RulebookDTO> responseDTO =
                PageResponseDTO.<RulebookDTO>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(totalCount)
                        .build();

        return responseDTO;
    }




    //작성

    public Integer createRulebook(RulebookDTO rulebookDTO){
        Rulebook rulebook = modelMapper.map(rulebookDTO, Rulebook.class);

    
        if (rulebookDTO.getImageUrl() != null) {
            rulebook.setImageUrl(rulebookDTO.getImageUrl());
        }

        rulebook.setContent(rulebookDTO.getContent());
        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setWriterId(rulebookDTO.getWriterId());
        System.out.println(rulebookDTO.getWriterId() + "--------------------");

        

        Rulebook savedRulebook = rulebookRepository.save(rulebook);

        return savedRulebook.getId();
    }


    //상세보기
    public RulebookDTO getRulebook(Integer id) {
        // 게시글 조회
        Optional<Rulebook> result = rulebookRepository.findById(id);
        Rulebook rulebook = result.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    
        // DTO로 변환하여 반환
        return modelMapper.map(rulebook, RulebookDTO.class);
    }
    

    //수정
    public void modifyRulebook(RulebookDTO rulebookDTO){
        Optional<Rulebook> result = rulebookRepository.findById(rulebookDTO.getId());

        Rulebook rulebook = result.orElseThrow();

        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setContent(rulebookDTO.getContent());

        rulebookRepository.save(rulebook);
    }

    //삭제
    public void deleteRulebook(Integer id){
        rulebookRepository.deleteById(id);
    }

      // 조회수 증가
      public void incrementViewCount(Integer id) {
        Optional<Rulebook> rulebookOpt = rulebookRepository.findById(id);
        
        // 게시글이 존재하는 경우 조회수 증가
        rulebookOpt.ifPresent(rulebook -> {
            rulebook.setViewCount(rulebook.getViewCount() + 1);
            rulebookRepository.save(rulebook);  // 조회수 증가 후 저장
        });
    }
}
