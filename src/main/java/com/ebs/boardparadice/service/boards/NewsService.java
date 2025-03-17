package com.ebs.boardparadice.service.boards;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.model.boards.Free;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.answers.AnswerDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.News;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.NewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final GamerRepository gamerRepository;
    private final ModelMapper modelMapper;


    // ✅ 뉴스 생성 (이미지 URL & 유튜브 링크 추가)
    public Integer createNews(NewsDTO newsDTO) {
        Optional<Gamer> gamerOpt = gamerRepository.findById(newsDTO.getWriterId());

       

        News news = new News();
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setWriterId(gamerOpt.get());
        

        // ✅ `voterIds`가 `null`이면 빈 `Set`으로 초기화하여 NullPointerException 방지
        Set<Integer> voterIds = newsDTO.getVoterIds() != null ? newsDTO.getVoterIds() : new HashSet<>();
        news.setVoter(voterIds.stream()
                .map(id -> gamerRepository.findById(id).orElseThrow())
                .collect(Collectors.toSet()));

        news.setImageUrls(newsDTO.getImageUrls()); // ✅ 이미지 저장
        news.setYoutubeUrl(newsDTO.getYoutubeUrl()); // ✅ 유튜브 링크 저장

        News savedNews = newsRepository.save(news);
        return savedNews.getId();
    }


    // ✅ 뉴스 조회 (이미지 URL & 유튜브 링크 포함)
    public NewsDTO getNews(Integer id) {
        Optional<News> result = newsRepository.findByIdWithAnswers(id);
        News news = result.orElseThrow();

        NewsDTO newsDTO = NewsDTO.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .writerId(news.getWriterId().getId())
                .voterIds(news.getVoter().stream().map(Gamer::getId).collect(Collectors.toSet()))
                .createdate(news.getCreatedate())
                .imageUrls(news.getImageUrls()) // ✅ 이미지 포함
                .youtubeUrl(news.getYoutubeUrl()) // ✅ 유튜브 링크 포함
                .build();
        // 댓글 리스트를 추가 (AnswerDTO 변환)
        newsDTO.setAnswerList(
            news.getAnswerList().stream()
                .map(answer -> AnswerDTO.builder()
                    .id(answer.getId())
                    .content(answer.getContent())
                    .gamer(answer.getGamer())
                    .createdate(answer.getCreatedate())
                    .voter(answer.getVoter())
                    .news(answer.getNews().getId())  // ✅ 뉴스 게시글 ID 저장
                    .build())
                .collect(Collectors.toList())
        );

        return newsDTO;
    }



    // ✅ 뉴스 수정
    public void modifyNews(NewsDTO newsDTO) {
        Optional<News> result = newsRepository.findById(newsDTO.getId());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 뉴스입니다.");
        }

        News news = result.get();
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setImageUrls(newsDTO.getImageUrls()); // ✅ 이미지 변경 가능
        news.setYoutubeUrl(newsDTO.getYoutubeUrl()); // ✅ 유튜브 링크 변경 가능

        newsRepository.save(news);
    }

    // ✅ 뉴스 삭제
    public void deleteNews(Integer id) {
        Optional<News> result = newsRepository.findById(id);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 뉴스입니다.");
        }

        newsRepository.deleteById(id);
    }

    // ✅ 뉴스 목록 조회 (페이징)
    public PageResponseDTO<NewsDTO> getlist(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("id").descending());

        Page<News> result = newsRepository.findAll(pageable);

        List<NewsDTO> dtoList = result.getContent().stream()
                .map(news -> modelMapper.map(news, NewsDTO.class)) // ✅ ModelMapper가 NewsDTO를 매핑할 수 있도록 수정됨
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        return PageResponseDTO.<NewsDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(totalCount)
                .build();
    }
/*
    public List<NewsDTO> getPostsByGamerId(int gamerId) {
        List<News> newsPosts = newsRepository.findByGamerId(gamerId);

        if (newsPosts.isEmpty()) {
            System.out.println("❌ 해당 유저가 작성한 글이 없습니다.");
        } else {
            System.out.println("✅ " + gamerId + " 사용자의 게시글 개수: " + newsPosts.size());
        }

        return newsPosts.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }*/
}
