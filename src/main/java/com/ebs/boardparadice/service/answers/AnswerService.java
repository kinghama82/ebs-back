package com.ebs.boardparadice.service.answers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.model.answers.FreeAnswer;
import com.ebs.boardparadice.model.answers.NewsAnswer;
import com.ebs.boardparadice.model.answers.QuestionAnswer;
import com.ebs.boardparadice.model.answers.RulebookAnswer;
import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.model.boards.News;
import com.ebs.boardparadice.model.boards.Question;
import com.ebs.boardparadice.model.boards.Rulebook;
import com.ebs.boardparadice.repository.answers.FreeAnswerRepository;
import com.ebs.boardparadice.repository.answers.NewsAnswerRepository;
import com.ebs.boardparadice.repository.answers.QuestionAnswerRepository;
import com.ebs.boardparadice.repository.answers.RulebookAnswerRepository;
import com.ebs.boardparadice.repository.boards.FreeRepository;
import com.ebs.boardparadice.repository.boards.NewsRepository;
import com.ebs.boardparadice.repository.boards.QuestionRepository;
import com.ebs.boardparadice.repository.boards.RulebookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final FreeAnswerRepository freeAnswerRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final RulebookAnswerRepository rulebookAnswerRepository;
    private final NewsAnswerRepository newsAnswerRepository;

    private final FreeRepository freeRepository;
    private final QuestionRepository questionRepository;
    private final RulebookRepository rulebookRepository;
    private final NewsRepository newsRepository;

    //등록
    @Transactional
    public AnswerDTO saveAnswer(String boardType, AnswerDTO answerDTO) {
        switch (boardType) {
            //자유
            case "free":
                Free free = freeRepository.findById(answerDTO.getFree())
                        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
                FreeAnswer freeAnswer = FreeAnswer.builder()
                        .content(answerDTO.getContent())
                        .gamer(answerDTO.getGamer())
                        .createdate(LocalDateTime.now())
                        .voter(answerDTO.getVoter())
                        .free(free)
                        .build();
                freeAnswerRepository.save(freeAnswer);
                return entityToDto(freeAnswer, boardType);
            //질문
            case "question":
                Question question = questionRepository.findById(answerDTO.getQuestion())
                        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
                QuestionAnswer questionAnswer = QuestionAnswer.builder()
                        .content(answerDTO.getContent())
                        .gamer(answerDTO.getGamer())
                        .createdate(LocalDateTime.now())
                        .voter(answerDTO.getVoter())
                        .question(question)
                        .build();
                questionAnswerRepository.save(questionAnswer);
                return entityToDto(questionAnswer, boardType);
            //룰북
            case "rulebook":
                Rulebook rulebook = rulebookRepository.findById(answerDTO.getRulebook())
                        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
                RulebookAnswer rulebookAnswer = RulebookAnswer.builder()
                        .content(answerDTO.getContent())
                        .gamer(answerDTO.getGamer())
                        .createdate(LocalDateTime.now())
                        .voter(answerDTO.getVoter())
                        .rulebook(rulebook)
                        .build();
                rulebookAnswerRepository.save(rulebookAnswer);
                return entityToDto(rulebookAnswer, boardType);
            //뉴스
            case "news":
                News news = newsRepository.findById(answerDTO.getNews())
                        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
                NewsAnswer newsAnswer = NewsAnswer.builder()
                        .content(answerDTO.getContent())
                        .gamer(answerDTO.getGamer())
                        .createdate(LocalDateTime.now())
                        .voter(answerDTO.getVoter())
                        .news(news)
                        .build();
                newsAnswerRepository.save(newsAnswer);
                return entityToDto(newsAnswer, boardType);

            default:
                throw new IllegalArgumentException("잘못된 게시판 타입");
        }
    }

    //삭제
    @Transactional
    public void deleteAnswer(String boardType, int id) {
        switch (boardType) {
            case "free":
                freeAnswerRepository.deleteById(id);
                break;
            case "question":
                questionAnswerRepository.deleteById(id);
                break;
            case "rulebook":
                rulebookAnswerRepository.deleteById(id);
                break;
            case "news":
                newsAnswerRepository.deleteById(id);
                break;
            default:
                throw new IllegalArgumentException("잘못된 보드타입" + boardType);
        }
    }

    // 엔티티 -> DTO
    private AnswerDTO entityToDto(Object answer, String boardType) {
        AnswerDTO.AnswerDTOBuilder dtoBuilder = AnswerDTO.builder();

        switch (boardType) {
            //자유
            case "free":
                FreeAnswer freeAnswer = (FreeAnswer) answer;
                return dtoBuilder
                        .id(freeAnswer.getId())
                        .content(freeAnswer.getContent())
                        .gamer(freeAnswer.getGamer())
                        .createdate(freeAnswer.getCreatedate())
                        .voter(freeAnswer.getVoter())
                        .free(freeAnswer.getFree().getId())
                        .build();
            //질문
            case "question":
                QuestionAnswer questionAnswer = (QuestionAnswer) answer;
                return dtoBuilder
                        .id(questionAnswer.getId())
                        .content(questionAnswer.getContent())
                        .gamer(questionAnswer.getGamer())
                        .createdate(questionAnswer.getCreatedate())
                        .voter(questionAnswer.getVoter())
                        .question(questionAnswer.getQuestion().getId())
                        .build();
            //룰북
            case "rulebook":
                RulebookAnswer rulebookAnswer = (RulebookAnswer) answer;
                return dtoBuilder
                        .id(rulebookAnswer.getId())
                        .content(rulebookAnswer.getContent())
                        .gamer(rulebookAnswer.getGamer())
                        .createdate(rulebookAnswer.getCreatedate())
                        .voter(rulebookAnswer.getVoter())
                        .rulebook(rulebookAnswer.getRulebook().getId())
                        .build();
            //뉴스
            case "news":
                NewsAnswer newsAnswer = (NewsAnswer) answer;
                return dtoBuilder
                        .id(newsAnswer.getId())
                        .content(newsAnswer.getContent())
                        .gamer(newsAnswer.getGamer())
                        .createdate(newsAnswer.getCreatedate())
                        .voter(newsAnswer.getVoter())
                        .news(newsAnswer.getNews().getId())
                        .build();

            default:
                throw new IllegalArgumentException("잘못된 게시판 타입: " + boardType);
        }
    }


    // ✅ 특정 사용자가 작성한 댓글을 모두 가져오기
    public Map<String, List<AnswerDTO>> getCommentsByGamerId(int gamerId) {
        return Map.of(
                "free", freeAnswerRepository.findByGamerId(gamerId)
                        .stream()
                        .map(answer -> AnswerDTO.builder()
                                .id(answer.getId())
                                .content(answer.getContent())
                                .gamer(answer.getGamer())
                                .createdate(answer.getCreatedate())
                                .voter(answer.getVoter())
                                .free(answer.getFree().getId())  // 자유게시판 ID 저장
                                .build())
                        .collect(Collectors.toList()),

                "question", questionAnswerRepository.findByGamerId(gamerId)
                        .stream()
                        .map(answer -> AnswerDTO.builder()
                                .id(answer.getId())
                                .content(answer.getContent())
                                .gamer(answer.getGamer())
                                .createdate(answer.getCreatedate())
                                .voter(answer.getVoter())
                                .question(answer.getQuestion().getId())  // 질문게시판 ID 저장
                                .build())
                        .collect(Collectors.toList()),

                "rulebook", rulebookAnswerRepository.findByGamerId(gamerId)
                        .stream()
                        .map(answer -> AnswerDTO.builder()
                                .id(answer.getId())
                                .content(answer.getContent())
                                .gamer(answer.getGamer())
                                .createdate(answer.getCreatedate())
                                .voter(answer.getVoter())
                                .rulebook(answer.getRulebook().getId())  // 룰북게시판 ID 저장
                                .build())
                        .collect(Collectors.toList()),

                "news", newsAnswerRepository.findByGamerId(gamerId)
                        .stream()
                        .map(answer -> AnswerDTO.builder()
                                .id(answer.getId())
                                .content(answer.getContent())
                                .gamer(answer.getGamer())
                                .createdate(answer.getCreatedate())
                                .voter(answer.getVoter())
                                .news(answer.getNews().getId())  // 뉴스게시판 ID 저장
                                .build())
                        .collect(Collectors.toList())
        );
    }



}
