package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.model.boards.News;
import com.ebs.boardparadice.repository.boards.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    private final ModelMapper modelMapper;

    public Integer createNews(NewsDTO newsDTO) {

        News news = modelMapper.map(newsDTO, News.class);

        News savedNews = newsRepository.save(news);

        return savedNews.getId();

    }

    public NewsDTO getNews(Integer id) {

        Optional<News> result = newsRepository.findById(id);

        News news = result.get();

        NewsDTO dto = modelMapper.map(news, NewsDTO.class);

        return dto;
    }

    public void modifyNews(NewsDTO newsDTO) {
        Optional<News> result = newsRepository.findById(newsDTO.getId());

        News news = result.orElseThrow();

        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());

        newsRepository.save(news);
    }

    public void deleteNews(Integer id) {
        newsRepository.deleteById(id);
    }

    public PageResponseDTO<NewsDTO> getlist(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("id").descending());

        Page<News> result = newsRepository.findAll(pageable);

        List<NewsDTO> dtoList = result.getContent().stream()
                .map(news -> modelMapper.map(news, NewsDTO.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<NewsDTO> responseDTO =
                PageResponseDTO.<NewsDTO>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(totalCount)
                        .build();
        return responseDTO;

    }



}
