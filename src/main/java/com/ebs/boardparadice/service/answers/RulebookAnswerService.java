package com.ebs.boardparadice.service.answers;

import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.answers.RulebookAnswer;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.answers.RulebookAnswerRepository;
import com.ebs.boardparadice.repository.boards.RulebookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RulebookAnswerService {
    private final RulebookAnswerRepository answerRepository;
    private final RulebookRepository rulebookRepository;
    private final GamerRepository gamerRepository;

    public List<RulebookAnswerDTO> getAnswersByRulebookId(int rulebookId) {
        return answerRepository.findByRulebookId(rulebookId)
                .stream().map(RulebookAnswer::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RulebookAnswerDTO addAnswer(int rulebookId, int writerId, String content) {
        Rulebook rulebook = rulebookRepository.findById(rulebookId)
                .orElseThrow(() -> new IllegalArgumentException("Rulebook not found"));
        Gamer writer = gamerRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RulebookAnswer answer = RulebookAnswer.builder()
                .content(content)
                .gamer(writer)
                .rulebook(rulebook)
//                .createdDate(LocalDateTime.now())
                .build();

        answerRepository.save(answer);
        return answer.toDTO();
    }

    @Transactional
    public void deleteAnswer(int answerId, int userId) {
        RulebookAnswer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        if (answer.getGamer().getId() != userId) {
            throw new IllegalStateException("You can only delete your own answers");
        }

        answerRepository.delete(answer);
    }
}
