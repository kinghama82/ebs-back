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
import com.ebs.boardparadice.DTO.boards.FreeDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Free;
import com.ebs.boardparadice.model.boards.FreeImage;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.FreeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FreeService {

   private final FreeRepository freeRepository;
   private final GamerRepository gamerRepository;

   //글 작성
   public int createFree(FreeDTO freeDTO) {
	   FreeDTO sanitizedFreeDTO = sanitizeContent(freeDTO);
	   Free free = dtoToEntity(sanitizedFreeDTO);
	   
	// ✅ 기존 이미지 리스트 초기화 (중복 방지)
       free.clearList();
	// ✅ 이미지 리스트 저장
       if (freeDTO.getUploadFileNames() != null && !freeDTO.getUploadFileNames().isEmpty()) {
    	   Set<String> uniqueImages = new HashSet<>(freeDTO.getUploadFileNames()); 
    	   for (String fileName : uniqueImages) {
               free.addImageString(fileName); // ✅ 여러 개의 이미지 추가
           }
       }
       System.out.println("📜 저장되는 게시글 데이터: " + free);
	   Free result = freeRepository.save(free);
	   
	   return result.getId();
   }
   
   //리스트
   public PageResponseDTO<FreeDTO> getList(PageRequestDTO pageRequestDTO) {
	    Pageable pageable = PageRequest.of(
	            pageRequestDTO.getPage() - 1,
	            pageRequestDTO.getSize(),
	            Sort.by("id").descending());

	    Page<Object[]> result = freeRepository.selectList(pageable);

	    List<FreeDTO> dtoList = result.get().map(arr -> {
	        Free free = (Free) arr[0];
	        FreeImage freeImage = (FreeImage) arr[1];
	        Long answerCount = (Long) arr[2];

	        FreeDTO freeDTO = FreeDTO.builder()
	                .id(free.getId())
	                .title(free.getTitle())
	                .content(free.getContent())
	                .gamer(free.getGamer())
	                .createdate(free.getCreatedate())
	                .view(free.getView())
	                .voter(free.getVoter())
	                .category(free.getCategory())
	                .build();

	        if (freeImage != null) {
	            String imageStr = freeImage.getFileName();
	            freeDTO.setUploadFileNames(List.of());
	        } else {
	            freeDTO.setUploadFileNames(List.of());
	        }

	        // ✅ 댓글 리스트를 `AnswerDTO`로 변경
	        freeDTO.setAnswerList(new ArrayList<>());
	        for (int i = 0; i < answerCount; i++) {
	            freeDTO.getAnswerList().add(
	                AnswerDTO.builder()
	                    .id(0)  
	                    .content("댓글 더미데이터")  
	                    .gamer(null)  
	                    .createdate(LocalDateTime.now())
	                    .voter(null)
	                    .free(free.getId())  
	                    .build()
	            );
	        }

	        return freeDTO;
	    }).collect(Collectors.toList());

	    Long totalCount = result.getTotalElements();

	    return PageResponseDTO.<FreeDTO>withAll()
	            .dtoList(dtoList)
	            .pageRequestDTO(pageRequestDTO)
	            .totalCount(totalCount)
	            .build();
	}
   
   //수정
   public void modifyFree(FreeDTO freeDTO) {
	   Optional<Free> result = freeRepository.findById(freeDTO.getId());
	   
	   Free free = result.orElseThrow();
	   
	   free.setContent(freeDTO.getContent());
	   free.setTitle(freeDTO.getTitle());
	   free.setCategory(freeDTO.getCategory());
	   
	   
	   free.clearList();
	   List<String> uploadFileNames = freeDTO.getUploadFileNames();
	   if(uploadFileNames != null && uploadFileNames.size() > 0) {
		   uploadFileNames.stream().forEach(uploadName -> {
			   free.addImageString(uploadName);
		   });
	   }	   
	   freeRepository.save(free);
   }
   
   //읽기
   public FreeDTO getFree(int id) {
	   Optional<Free> result = freeRepository.findByIdWithAnswers(id);
	   Free free = result.orElseThrow();
	   
	   FreeDTO freeDTO = entityToDTO(free);
	   
	// 댓글 리스트를 변환하여 DTO에 추가
	    freeDTO.setAnswerList(
	        free.getAnswerList().stream()
	            .map(answer -> AnswerDTO.builder()
	            		.id(answer.getId())
	            		.content(answer.getContent())
	                    .gamer(answer.getGamer())
	                    .createdate(answer.getCreatedate())
	                    .voter(answer.getVoter())
	                    .free(answer.getFree().getId())
	                    .build())
	            .collect(Collectors.toList())
	    );
	   
	   return freeDTO;
   }
   
   //삭제
   public void deleteFree(int id) {
	   freeRepository.deleteById(id);
   }

   //추천수 증가
   public void plusFreeVote(int freeId, int gamerId) {
	   Optional<Free> result1 = freeRepository.findById(freeId);
	   Optional<Gamer> result2 = gamerRepository.findById(gamerId);
	   if(result1.isEmpty() || result2.isEmpty()) {
		   throw new RuntimeException("게시글 또는 유저를 찾을 수 없습니다");
	   }
	   Free free = result1.get();
	   Gamer gamer = result2.get();
	   
	   if(free.getVoter().contains(gamer)) {
		   throw new RuntimeException("이미 추천한 유저입니다.");
	   }
	   
	   free.getVoter().add(gamer);
	   freeRepository.save(free);
   }
   //추천수 탑5
   public List<FreeDTO> getVoteTop5(){
	   Pageable pageable = PageRequest.of(0, 5);
	   List<Free> vote5List = freeRepository.findByVoteTop5(pageable);
	   return vote5List.stream()
			   .map(this::entityToDTO)
			   .collect(Collectors.toList());
   }
   //조회수 탑5
   public List<FreeDTO> getViewTop5(){
	     Pageable pageable = PageRequest.of(0, 5);
	     List<Free> view5List = freeRepository.findByViewTop5(pageable);
	     return view5List.stream()
	    		 .map(this::entityToDTO)
	    		 .collect(Collectors.toList());	     
   }
   //조회수증가
   public void plusFreeView(int id) {
	   Optional<Free> result = freeRepository.findById(id);
	   if(result.isPresent()) {
		   Free free = result.get();
		   free.setView(free.getView()+1);
		   freeRepository.save(free);
	   }
   }
   
   //dto -> entity
   public Free dtoToEntity(FreeDTO freeDTO) {
	   
	   Free free = Free.builder()
			   .id(freeDTO.getId())
			   .title(freeDTO.getTitle())
			   .gamer(freeDTO.getGamer())
			   .content(freeDTO.getContent())
			   .createdate(LocalDateTime.now())
			   .voter(freeDTO.getVoter())
			   .category(freeDTO.getCategory())
			   .build();
	   
	// ✅ 이미지 리스트 저장 (이전에는 `imageList`가 저장되지 않았음)
       if (freeDTO.getUploadFileNames() != null && !freeDTO.getUploadFileNames().isEmpty()) {
           freeDTO.getUploadFileNames().forEach(free::addImageString);
       }
	   return free;
   }
   //entity -> dto
   public FreeDTO entityToDTO(Free free) {
	   FreeDTO freeDTO = FreeDTO.builder()
			   .id(free.getId())
			   .title(free.getTitle())
			   .gamer(free.getGamer())
			   .content(free.getContent())
			   .createdate(free.getCreatedate())
			   .view(free.getView())
			   .category(free.getCategory())
			   .answerList(
					   free.getAnswerList() != null ?
					   free.getAnswerList().stream()
					   .map(answer -> AnswerDTO.builder()
		                        .id(answer.getId())
		                        .content(answer.getContent())
		                        .gamer(answer.getGamer())
		                        .createdate(answer.getCreatedate())
		                        .voter(answer.getVoter())
		                        .free(answer.getFree().getId())  // ✅ 자유게시판이므로 `free` 필드 사용
		                        .build())
		                    .collect(Collectors.toList())
		                : new ArrayList<>())  // ✅ `answerList`가 없으면 빈 리스트 반환
		            .voter(free.getVoter())
		            .build();
	   List<FreeImage> imageList = free.getImageList();
	   if (imageList != null && !imageList.isEmpty()) {
           List<String> fileNameList = imageList.stream()
                   .map(FreeImage::getFileName)
                   .toList();
           freeDTO.setUploadFileNames(fileNameList);
       }
	   return freeDTO;
   }
   
   public FreeDTO sanitizeContent(FreeDTO freeDTO) {
	    String content = freeDTO.getContent();
	    

	 // ✅ <img> 태그의 src 속성만 유지
	    content = content.replaceAll("(?i)<img\\s+[^>]*src=['\"]([^'\"]+)['\"][^>]*>", "<img src=\"$1\">");

	    // ✅ <iframe> 태그의 src 속성만 유지 (div를 포함하고 있으면 div를 제거)
	    content = content.replaceAll("(?i)<div[^>]*data-youtube-video[^>]*>(<iframe[^>]*src=['\"]([^'\"]+)['\"][^>]*></iframe>)</div>", "$1");

	    // ✅ `<script>` 같은 위험한 태그 제거
	    content = content.replaceAll("(?i)<script.*?</script>", "");

	    
	    freeDTO.setContent(content);
	    return freeDTO;
	}

	public List<FreeDTO> getPostsByGamerId(int gamerId) {
		List<Free> freePosts = freeRepository.findByGamerId(gamerId);

		if (freePosts.isEmpty()) {
			System.out.println("❌ 해당 유저가 작성한 글이 없습니다.");
		} else {
			System.out.println("✅ " + gamerId + " 사용자의 게시글 개수: " + freePosts.size());
		}

		return freePosts.stream()
				.map(this::entityToDTO)
				.collect(Collectors.toList());
	}


}
