package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.RulebookRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RulebookService {

    private final RulebookRepository rulebookRepository;
    private final ModelMapper modelMapper;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

       // 이미지 업로드 메서드
    public String uploadImage(MultipartFile image) {
        try {
            // 이미지 파일명 처리
            String fileName = StringUtils.cleanPath(image.getOriginalFilename());
            File targetFile = new File(uploadDir + File.separator + fileName);

            // 파일을 서버에 저장
            image.transferTo(targetFile);

            // 파일 URL 생성 (클라이언트가 이미지를 접근할 수 있도록 URL 반환)
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/upload/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
   

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

    public Integer createRulebook(RulebookDTO rulebookDTO){
        Rulebook rulebook = modelMapper.map(rulebookDTO, Rulebook.class);

        if (rulebookDTO.getImageUrl() != null) {
            rulebook.setImageUrl(rulebookDTO.getImageUrl());
        }

        rulebook.setContent(rulebookDTO.getContent());
        Rulebook savedRulebook = rulebookRepository.save(rulebook);

        return savedRulebook.getId();
    }

    public RulebookDTO getRulebook(Integer id){

        Optional<Rulebook> result = rulebookRepository.findById(id);

        Rulebook rulebook = result.get();

        RulebookDTO dto = modelMapper.map(rulebook, RulebookDTO.class);

        return dto;
    }

    public void modifyRulebook(RulebookDTO rulebookDTO){
        Optional<Rulebook> result = rulebookRepository.findById(rulebookDTO.getId());

        Rulebook rulebook = result.orElseThrow();

        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setContent(rulebookDTO.getContent());

        rulebookRepository.save(rulebook);
    }

    public void deleteRulebook(Integer id){
        rulebookRepository.deleteById(id);
    }

}
