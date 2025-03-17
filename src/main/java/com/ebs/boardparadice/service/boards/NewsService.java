package com.ebs.boardparadice.service.boards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.ebs.boardparadice.model.boards.NewsImage;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.NewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsService {

	private final NewsRepository newsRepository;
	private final GamerRepository gamerRepository;
	
	//글 작성
	   public int createNews(NewsDTO newsDTO) {
		   NewsDTO sanitizedFreeDTO = sanitizeContent(newsDTO);
		   News news = dtoToEntity(sanitizedFreeDTO);
		   
		// ✅ 기존 이미지 리스트 초기화 (중복 방지)
	       news.clearList();
		// ✅ 이미지 리스트 저장
	       if (newsDTO.getUploadFileNames() != null && !newsDTO.getUploadFileNames().isEmpty()) {
	    	   Set<String> uniqueImages = new HashSet<>(newsDTO.getUploadFileNames()); 
	    	   for (String fileName : uniqueImages) {
	               news.addImageString(fileName); // ✅ 여러 개의 이미지 추가
	           }
	       }
		   News result = newsRepository.save(news);
		   
		   return result.getId();
	   }
	   
	   //리스트
	   public PageResponseDTO<NewsDTO> getList(PageRequestDTO pageRequestDTO) {
		    Pageable pageable = PageRequest.of(
		            pageRequestDTO.getPage() - 1,
		            pageRequestDTO.getSize(),
		            Sort.by("id").descending());

		    Page<Object[]> result = newsRepository.selectList(pageable);

		    List<NewsDTO> dtoList = result.get().map(arr -> {
		        News news = (News) arr[0];
		        NewsImage newsImage = (NewsImage) arr[1];
		        Long answerCount = (Long) arr[2];

		        NewsDTO newsDTO = NewsDTO.builder()
		                .id(news.getId())
		                .title(news.getTitle())
		                .content(news.getContent())
		                .gamer(news.getGamer())
		                .createdate(news.getCreatedate())
		                .view(news.getView())
		                .voter(news.getVoter())
		                .build();

		        if (newsImage != null) {
		            String imageStr = newsImage.getFileName();
		            newsDTO.setUploadFileNames(List.of());
		        } else {
		            newsDTO.setUploadFileNames(List.of());
		        }

		        // ✅ 댓글 리스트를 `AnswerDTO`로 변경
		        newsDTO.setAnswerList(new ArrayList<>());
		        for (int i = 0; i < answerCount; i++) {
		            newsDTO.getAnswerList().add(
		                AnswerDTO.builder()
		                    .id(0)  
		                    .content("댓글 더미데이터")  
		                    .gamer(null)  
		                    .createdate(LocalDateTime.now())
		                    .voter(null)
		                    .news(news.getId())  
		                    .build()
		            );
		        }

		        return newsDTO;
		    }).collect(Collectors.toList());

		    Long totalCount = result.getTotalElements();

		    return PageResponseDTO.<NewsDTO>withAll()
		            .dtoList(dtoList)
		            .pageRequestDTO(pageRequestDTO)
		            .totalCount(totalCount)
		            .build();
		}
	   
	   //수정
	   public void modifyNews(NewsDTO newsDTO) {
		   Optional<News> result = newsRepository.findById(newsDTO.getId());
		   
		   News news = result.orElseThrow();
		   
		   news.setContent(newsDTO.getContent());
		   news.setTitle(newsDTO.getTitle());
		   
		   
		   news.clearList();
		   List<String> uploadFileNames = newsDTO.getUploadFileNames();
		   if(uploadFileNames != null && uploadFileNames.size() > 0) {
			   uploadFileNames.stream().forEach(uploadName -> {
				   news.addImageString(uploadName);
			   });
		   }	   
		   newsRepository.save(news);
	   }
	   
	   //읽기
	   public NewsDTO getNews(int id) {
		   Optional<News> result = newsRepository.findByIdWithAnswers(id);
		   News news = result.orElseThrow();
		   
		   NewsDTO newsDTO = entityToDTO(news);
		   
		// 댓글 리스트를 변환하여 DTO에 추가
		    newsDTO.setAnswerList(
		        news.getAnswerList().stream()
		            .map(answer -> AnswerDTO.builder()
		            		.id(answer.getId())
		            		.content(answer.getContent())
		                    .gamer(answer.getGamer())
		                    .createdate(answer.getCreatedate())
		                    .voter(answer.getVoter())
		                    .free(answer.getNews().getId())
		                    .build())
		            .collect(Collectors.toList())
		    );
		   
		   return newsDTO;
	   }
	   
	   //삭제
	   public void deleteFree(int id) {
		   newsRepository.deleteById(id);
	   }

	   //추천수 증가
	   public void plusFreeVote(int newsId, int gamerId) {
		   Optional<News> result1 = newsRepository.findById(newsId);
		   Optional<Gamer> result2 = gamerRepository.findById(gamerId);
		   if(result1.isEmpty() || result2.isEmpty()) {
			   throw new RuntimeException("게시글 또는 유저를 찾을 수 없습니다");
		   }
		   News news = result1.get();
		   Gamer gamer = result2.get();
		   
		   if(news.getVoter().contains(gamer)) {
			   throw new RuntimeException("이미 추천한 유저입니다.");
		   }
		   
		   news.getVoter().add(gamer);
		   newsRepository.save(news);
	   }
	   //추천수 탑5
	   public List<NewsDTO> getVoteTop5(){
		   Pageable pageable = PageRequest.of(0, 5);
		   List<News> vote5List = newsRepository.findByVoteTop5(pageable);
		   return vote5List.stream()
				   .map(this::entityToDTO)
				   .collect(Collectors.toList());
	   }
	   //조회수 탑5
	   public List<NewsDTO> getViewTop5(){
		     Pageable pageable = PageRequest.of(0, 5);
		     List<News> view5List = newsRepository.findByViewTop5(pageable);
		     return view5List.stream()
		    		 .map(this::entityToDTO)
		    		 .collect(Collectors.toList());	     
	   }
	   //조회수증가
	   public void plusFreeView(int id) {
		   Optional<News> result = newsRepository.findById(id);
		   if(result.isPresent()) {
			   News news = result.get();
			   news.setView(news.getView()+1);
			   newsRepository.save(news);
		   }
	   }
	   
	   //dto -> entity
	   public News dtoToEntity(NewsDTO newsDTO) {
		   
		   News news = News.builder()
				   .id(newsDTO.getId())
				   .title(newsDTO.getTitle())
				   .gamer(newsDTO.getGamer())
				   .content(newsDTO.getContent())
				   .createdate(LocalDateTime.now())
				   .voter(newsDTO.getVoter())
				   .build();
		   
		// ✅ 이미지 리스트 저장 (이전에는 `imageList`가 저장되지 않았음)
	       if (newsDTO.getUploadFileNames() != null && !newsDTO.getUploadFileNames().isEmpty()) {
	           newsDTO.getUploadFileNames().forEach(news::addImageString);
	       }
		   return news;
	   }
	   //entity -> dto
	   public NewsDTO entityToDTO(News news) {
		   NewsDTO newsDTO = NewsDTO.builder()
				   .id(news.getId())
				   .title(news.getTitle())
				   .gamer(news.getGamer())
				   .content(news.getContent())
				   .createdate(news.getCreatedate())
				   .view(news.getView())
				   .answerList(
						   news.getAnswerList() != null ?
						   news.getAnswerList().stream()
						   .map(answer -> AnswerDTO.builder()
			                        .id(answer.getId())
			                        .content(answer.getContent())
			                        .gamer(answer.getGamer())
			                        .createdate(answer.getCreatedate())
			                        .voter(answer.getVoter())
			                        .free(answer.getNews().getId())  // ✅ 자유게시판이므로 `free` 필드 사용
			                        .build())
			                    .collect(Collectors.toList())
			                : new ArrayList<>())  // ✅ `answerList`가 없으면 빈 리스트 반환
			            .voter(news.getVoter())
			            .build();
		   List<NewsImage> imageList = news.getImageList();
		   if (imageList != null && !imageList.isEmpty()) {
	           List<String> fileNameList = imageList.stream()
	                   .map(NewsImage::getFileName)
	                   .toList();
	           newsDTO.setUploadFileNames(fileNameList);
	       }
		   return newsDTO;
	   }
	   
	   public NewsDTO sanitizeContent(NewsDTO newsDTO) {
		    String content = newsDTO.getContent();

		 // ✅ <img> 태그의 src 속성만 유지
		    content = content.replaceAll("(?i)<img\\s+[^>]*src=['\"]([^'\"]+)['\"][^>]*>", "<img src=\"$1\">");

		    // ✅ <iframe> 태그의 src 속성만 유지 (div를 포함하고 있으면 div를 제거)
		    content = content.replaceAll("(?i)<div[^>]*data-youtube-video[^>]*>(<iframe[^>]*src=['\"]([^'\"]+)['\"][^>]*></iframe>)</div>", "$1");

		    // ✅ `<script>` 같은 위험한 태그 제거
		    content = content.replaceAll("(?i)<script.*?</script>", "");

		    newsDTO.setContent(content);
		    System.out.println("최종 저장 백엔드 : " + newsDTO.getContent());
		    return newsDTO;
		}

	
}
