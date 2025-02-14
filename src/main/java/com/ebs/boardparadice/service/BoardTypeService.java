package com.ebs.boardparadice.service;

import com.ebs.boardparadice.model.BoardType;
import com.ebs.boardparadice.repository.BoardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardTypeService {

    private final BoardTypeRepository boardTypeRepository;

    public List<BoardType> getList() {
        return boardTypeRepository.findAll();
    }
}
