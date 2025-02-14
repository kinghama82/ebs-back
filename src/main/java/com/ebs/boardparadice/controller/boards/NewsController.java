package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.service.boards.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/{id}")
    public NewsDTO get(@PathVariable(name="id") Integer id){
        return newsService.getNews(id);
    }

    @GetMapping("/list")
    public PageResponseDTO<NewsDTO> list(PageRequestDTO pageRequestDTO){
        return newsService.getlist(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Integer> create(@RequestBody NewsDTO newsDTO){

        Integer id = newsService.createNews(newsDTO);
            return Map.of("id", id);

    }

    @PutMapping("/{id}")
    public Map<String, String> modify(@PathVariable(name="id") Integer id, @RequestBody NewsDTO newsDTO){
        newsDTO.setId(id);

        newsService.modifyNews(newsDTO);
        return Map.of("result", "성공");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable(name="id") Integer id){
        newsService.deleteNews(id);
        return Map.of("결과", "성공");
    }

}
