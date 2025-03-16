package com.ebs.boardparadice.service.answers;

import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.RulebookAnswer;
import com.ebs.boardparadice.model.boards.Rulebook;

import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.answers.RulebookAnswerRepository;

import com.ebs.boardparadice.repository.boards.RulebookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RulebookAnswerService {

    private final RulebookAnswerRepository answerRepository;
    private final RulebookRepository rulebookRepository;
    private final GamerRepository gamerRepository;
    private final ModelMapper modelMapper;

    /**
     * 특정 게시글의 답글 목록 조회
     */
    public List<RulebookAnswerDTO> getAnswersByRulebookId(int rulebookId) {
        return answerRepository.findByRulebookId(rulebookId).stream()
                .map(answer -> modelMapper.map(answer, RulebookAnswerDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * 답글 추가
     */
    public RulebookAnswerDTO addAnswer(int rulebookId, int writerId, String content) {
        Rulebook rulebook = rulebookRepository.findById(rulebookId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Gamer writer = gamerRepository.findById(writerId)
                .orElseThrow(() -> new RuntimeException("작성자를 찾을 수 없습니다."));

        RulebookAnswer answer = new RulebookAnswer();
        answer.setRulebook(rulebook);
        answer.setGamer(writer);
        answer.setContent(content);
        answer.setCreatedate(LocalDateTime.now());

        return modelMapper.map(answerRepository.save(answer), RulebookAnswerDTO.class);
    }
    /** 답글 수정 */
    public RulebookAnswerDTO updateAnswer(int answerId, String content) {
        RulebookAnswer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("답글을 찾을 수 없습니다."));

        answer.setContent(content);

        return modelMapper.map(answerRepository.save(answer), RulebookAnswerDTO.class);
    }

    /**
     * 답글 삭제
     */
    public void deleteAnswer(int answerId) {
        answerRepository.deleteById(answerId);
    }
}
