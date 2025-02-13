package com.ebs.boardparadice.service;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.RulebookDTO;
import com.ebs.boardparadice.model.Rulebook;
import com.ebs.boardparadice.repository.RulebookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RulebookService {

    private final RulebookRepository rulebookRepository;
    private final ModelMapper modelMapper;

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

    public Integer register(RulebookDTO rulebookDTO){
        Rulebook rulebook = modelMapper.map(rulebookDTO, Rulebook.class);

        Rulebook savedRulebook = rulebookRepository.save(rulebook);

        return savedRulebook.getId();
    }

    public RulebookDTO get(Integer id){

        Optional<Rulebook> result = rulebookRepository.findById(id);

        Rulebook rulebook = result.get();

        RulebookDTO dto = modelMapper.map(rulebook, RulebookDTO.class);

        return dto;
    }

    public void modify(RulebookDTO rulebookDTO){
        Optional<Rulebook> result = rulebookRepository.findById(rulebookDTO.getId());

        Rulebook rulebook = result.orElseThrow();

        rulebook.setTitle(rulebookDTO.getTitle());
        rulebook.setContent(rulebookDTO.getContent());

        rulebookRepository.save(rulebook);
    }

    public void remove(Integer id){
        rulebookRepository.deleteById(id);
    }

}
