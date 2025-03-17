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
import com.ebs.boardparadice.DTO.boards.QuestionDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.boards.Question;
import com.ebs.boardparadice.model.boards.QuestionImage;
import com.ebs.boardparadice.repository.GamerRepository;
import com.ebs.boardparadice.repository.boards.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final GamerRepository gamerRepository;

	// 글 작성
	public int createQuestion(QuestionDTO questionDTO) {
		QuestionDTO sanitizedFreeDTO = sanitizeContent(questionDTO);
		Question question = dtoToEntity(sanitizedFreeDTO);

		// ✅ 기존 이미지 리스트 초기화 (중복 방지)
		question.clearList();
		// ✅ 이미지 리스트 저장
		if (questionDTO.getUploadFileNames() != null && !questionDTO.getUploadFileNames().isEmpty()) {
			Set<String> uniqueImages = new HashSet<>(questionDTO.getUploadFileNames());
			for (String fileName : uniqueImages) {
				question.addImageString(fileName); // ✅ 여러 개의 이미지 추가
			}
		}
		System.out.println("📜 저장되는 게시글 데이터: " + question);
		Question result = questionRepository.save(question);

		return result.getId();
	}

	// 리스트
	public PageResponseDTO<QuestionDTO> getList(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
							pageRequestDTO.getSize(),
							Sort.by("id").descending());

		Page<Object[]> result = questionRepository.selectList(pageable);

		List<QuestionDTO> dtoList = result.get().map(arr -> {
			Question question = (Question) arr[0];
			QuestionImage questionImage = (QuestionImage) arr[1];
			Long answerCount = (Long) arr[2];

			QuestionDTO questionDTO = QuestionDTO.builder()
							.id(question.getId())
							.title(question.getTitle())
							.content(question.getContent())
							.gamer(question.getGamer())
							.createdate(question.getCreatedate())
							.view(question.getView())
							.voter(question.getVoter())
							.build();

			if (questionImage != null) {
				String imageStr = questionImage.getFileName();
				questionDTO.setUploadFileNames(List.of());
			} else {
				questionDTO.setUploadFileNames(List.of());
			}

			// ✅ 댓글 리스트를 `AnswerDTO`로 변경
			questionDTO.setAnswerList(new ArrayList<>());
			for (int i = 0; i < answerCount; i++) {
				questionDTO.getAnswerList().add(AnswerDTO.builder()
						.id(0).content("댓글 더미데이터")
						.gamer(null)
						.createdate(LocalDateTime.now())
						.voter(null)
						.question(question.getId())
						.build());
			}

			return questionDTO;
		}).collect(Collectors.toList());

		Long totalCount = result.getTotalElements();

		return PageResponseDTO.<QuestionDTO>withAll()
				.dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO)
				.totalCount(totalCount)
				.build();
	}

	// 수정
	public void modifyQuestion(QuestionDTO questionDTO) {
		Optional<Question> result = questionRepository.findById(questionDTO.getId());

		Question question = result.orElseThrow();

		question.setContent(questionDTO.getContent());
		question.setTitle(questionDTO.getTitle());

		question.clearList();
		List<String> uploadFileNames = questionDTO.getUploadFileNames();
		if (uploadFileNames != null && uploadFileNames.size() > 0) {
			uploadFileNames.stream().forEach(uploadName -> {
				question.addImageString(uploadName);
			});
		}
		questionRepository.save(question);
	}

	// 읽기
	public QuestionDTO getQuestion(int id) {
		Optional<Question> result = questionRepository.findByIdWithAnswers(id);
		Question question = result.orElseThrow();

		QuestionDTO questionDTO = entityToDTO(question);

		// 댓글 리스트를 변환하여 DTO에 추가
		questionDTO.setAnswerList(question.getAnswerList().stream()
				.map(answer -> AnswerDTO.builder()
						.id(answer.getId())
						.content(answer.getContent())
						.gamer(answer.getGamer())
						.createdate(answer.getCreatedate())
						.voter(answer.getVoter())
						.question(answer.getQuestion().getId())
						.build())
				.collect(Collectors.toList()));

		return questionDTO;
	}

	// 삭제
	public void deleteFree(int id) {
		questionRepository.deleteById(id);
	}

	// 추천수 증가
	public void plusFreeVote(int questionId, int gamerId) {
		Optional<Question> result1 = questionRepository.findById(questionId);
		Optional<Gamer> result2 = gamerRepository.findById(gamerId);
		if (result1.isEmpty() || result2.isEmpty()) {
			throw new RuntimeException("게시글 또는 유저를 찾을 수 없습니다");
		}
		Question question = result1.get();
		Gamer gamer = result2.get();

		if (question.getVoter().contains(gamer)) {
			throw new RuntimeException("이미 추천한 유저입니다.");
		}

		question.getVoter().add(gamer);
		questionRepository.save(question);
	}

	// 추천수 탑5
	public List<QuestionDTO> getVoteTop5() {
		Pageable pageable = PageRequest.of(0, 5);
		List<Question> vote5List = questionRepository.findByVoteTop5(pageable);
		return vote5List.stream().map(this::entityToDTO).collect(Collectors.toList());
	}

	// 조회수 탑5
	public List<QuestionDTO> getViewTop5() {
		Pageable pageable = PageRequest.of(0, 5);
		List<Question> view5List = questionRepository.findByViewTop5(pageable);
		return view5List.stream().map(this::entityToDTO).collect(Collectors.toList());
	}

	// 조회수증가
	public void plusFreeView(int id) {
		Optional<Question> result = questionRepository.findById(id);
		if (result.isPresent()) {
			Question question = result.get();
			question.setView(question.getView() + 1);
			questionRepository.save(question);
		}
	}

	// dto -> entity
	public Question dtoToEntity(QuestionDTO questionDTO) {

		Question question = Question.builder()
					.id(questionDTO.getId())
					.title(questionDTO.getTitle())
					.gamer(questionDTO.getGamer())
					.content(questionDTO.getContent())
					.createdate(LocalDateTime.now())
					.voter(questionDTO.getVoter())
					.build();

		// ✅ 이미지 리스트 저장 (이전에는 `imageList`가 저장되지 않았음)
		if (questionDTO.getUploadFileNames() != null && 
				!questionDTO.getUploadFileNames().isEmpty()) {
			questionDTO.getUploadFileNames().forEach(question::addImageString);
		}
		return question;
	}

	// entity -> dto
	public QuestionDTO entityToDTO(Question question) {
		QuestionDTO questionDTO = QuestionDTO
				.builder().id(question.getId()).title(
						question.getTitle())
				.gamer(question.getGamer())
				.content(question.getContent()).createdate(question.getCreatedate())
				.view(question.getView())
				.answerList(question.getAnswerList() != null ? question.getAnswerList().stream()
						.map(answer -> AnswerDTO.builder()
								.id(answer.getId())
								.content(answer.getContent())
								.gamer(answer.getGamer())
								.createdate(answer.getCreatedate())
								.voter(answer.getVoter())
								.question(answer.getQuestion().getId()) // ✅ 자유게시판이므로 `free` 필드 사용
								.build())
						.collect(Collectors.toList()) : new ArrayList<>()) // ✅ `answerList`가 없으면 빈 리스트 반환
				.voter(question.getVoter()).build();
		List<QuestionImage> imageList = question.getImageList();
		if (imageList != null && !imageList.isEmpty()) {
			List<String> fileNameList = imageList.stream().map(QuestionImage::getFileName).toList();
			questionDTO.setUploadFileNames(fileNameList);
		}
		return questionDTO;
	}

	public QuestionDTO sanitizeContent(QuestionDTO questionDTO) {
		String content = questionDTO.getContent();

		// ✅ <img> 태그의 src 속성만 유지
		content = content.replaceAll("(?i)<img\\s+[^>]*src=['\"]([^'\"]+)['\"][^>]*>", "<img src=\"$1\">");

		// ✅ <iframe> 태그의 src 속성만 유지 (div를 포함하고 있으면 div를 제거)
		content = content.replaceAll(
				"(?i)<div[^>]*data-youtube-video[^>]*>(<iframe[^>]*src=['\"]([^'\"]+)['\"][^>]*></iframe>)</div>",
				"$1");

		// ✅ `<script>` 같은 위험한 태그 제거
		content = content.replaceAll("(?i)<script.*?</script>", "");

		questionDTO.setContent(content);
		return questionDTO;
	}

}
