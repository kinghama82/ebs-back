package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.boards.RulebookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RulebookService {

    private final RulebookRepository rulebookRepository;
    private final ModelMapper modelMapper;

    // 리스트
    public PageResponseDTO<RulebookDTO> getList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("id").descending());

        Page<Rulebook> result = rulebookRepository.findAll(pageable);

        List<RulebookDTO> dtoList = result.getContent().stream()
                .map(rulebook -> modelMapper.map(rulebook, RulebookDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        return PageResponseDTO.<RulebookDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }

    // 작성 (이미지 업로드와 함께)
    public Integer createRulebook(RulebookDTO rulebookDTO) {
        Rulebook rulebook = modelMapper.map(rulebookDTO, Rulebook.class);

        // 이미지 URL과 YouTube 링크를 직접 저장
        rulebook.setImageUrls(rulebookDTO.getImageUrls());
        rulebook.setYoutubeLinks(rulebookDTO.getYoutubeLinks());

        Rulebook savedRulebook = rulebookRepository.save(rulebook);
        return savedRulebook.getId();
    }


    // 이미지 업로드
    public String uploadImage(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/upload/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();  // 업로드 디렉토리가 없으면 생성
        }

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        // 반환할 URL
        return "http://localhost:8080/upload/" + fileName;
    }

    // 상세보기
    public RulebookDTO getRulebook(Integer id) {
        // 게시글 조회
        Optional<Rulebook> result = rulebookRepository.findById(id);
        Rulebook rulebook = result.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // DTO로 변환하여 반환
        return modelMapper.map(rulebook, RulebookDTO.class);
    }

    // 수정
    public void modifyRulebook(RulebookDTO rulebookDTO) {
        Optional<Rulebook> result = rulebookRepository.findById(rulebookDTO.getId());

        Rulebook rulebook = result.orElseThrow();

        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setContent(rulebookDTO.getContent());

        rulebookRepository.save(rulebook);
    }

    // 삭제
    public void deleteRulebook(Integer id) {
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
