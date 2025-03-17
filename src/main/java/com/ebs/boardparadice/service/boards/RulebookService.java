package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.GamerRepository;
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
    private final GamerRepository gamerRepository;

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
        rulebook.setWriter(rulebookDTO.getWriter());

        Rulebook savedRulebook = rulebookRepository.save(rulebook);
        return savedRulebook.getId();
    }

    // 상세보기
    public RulebookDTO getRulebook(Integer id) {
        // 게시글 조회
        Optional<Rulebook> result = rulebookRepository.findById(id);
        Rulebook rulebook = result.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        //댓리스트를 dto에 추가
        RulebookDTO rulebookDTO = modelMapper.map(rulebook, RulebookDTO.class);
        
        rulebookDTO.setAnswerList(
        		rulebook.getAnswerList().stream()
        		.map(answer -> modelMapper.map(answer, AnswerDTO.class))
        		.collect(Collectors.toList()));
        
        // DTO로 변환하여 반환
        return rulebookDTO;
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

    // 추천수 증가
    public void incrementVoteCount(Integer rulebookId, Integer gamerId) {
        Optional<Rulebook> rulebookOpt = rulebookRepository.findById(rulebookId);
        Optional<Gamer> gamerOpt = gamerRepository.findById(gamerId);

        if (rulebookOpt.isEmpty() || gamerOpt.isEmpty()) {
            throw new RuntimeException("게시글 또는 유저를 찾을 수 없습니다.");
        }

        Rulebook rulebook = rulebookOpt.get();
        Gamer gamer = gamerOpt.get();

        // 이미 추천한 유저인지 체크
        if (rulebook.getVoter().contains(gamer)) {
            throw new RuntimeException("이미 추천한 유저입니다.");
        }

        // 추천수 증가 및 유저 추가
        rulebook.setVoteCount(rulebook.getVoteCount() + 1);
        rulebook.getVoter().add(gamer);  // 추천한 유저를 voter 리스트에 추가

        rulebookRepository.save(rulebook);
    }

    // 제목으로 룰북 검색
    public List<RulebookDTO> searchRulebooksByTitle(String title) {
        return rulebookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(rulebook -> modelMapper.map(rulebook, RulebookDTO.class))
                .collect(Collectors.toList());
    }


}
