package com.ebs.boardparadice.service.boards;

import org.springframework.stereotype.Service;

import com.ebs.boardparadice.repository.boards.FreeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeService {

    private final FreeRepository freeRepository;

   

}
