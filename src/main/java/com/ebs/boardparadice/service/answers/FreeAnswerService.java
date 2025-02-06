package com.ebs.boardparadice.service.answers;


import com.ebs.boardparadice.model.Free;
import com.ebs.boardparadice.model.answers.FreeAnswer;
import com.ebs.boardparadice.repository.answers.FreeAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreeAnswerService {

    private final FreeAnswerRepository freeAnswerRepository;

    public FreeAnswer getFreeAnswer(Integer freeId) {
        return freeAnswerRepository.findById(id).orElse(null);
    }
}
