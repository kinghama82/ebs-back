package com.ebs.boardparadice.service.boards;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.HistoryDTO;
import com.ebs.boardparadice.model.boards.Game;
import com.ebs.boardparadice.model.boards.History;
import com.ebs.boardparadice.repository.boards.HistoryRepository;
import com.ebs.boardparadice.service.GamerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final GameService gameService;
	private final GamerService gamerService;
	private final ModelMapper modelMapper;
	
	
	//등록
	public Integer createHistory(HistoryDTO historyDTO) {
		History history = modelMapper.map(historyDTO, History.class);
		History saveHistory = historyRepository.save(history);
		
		return saveHistory.getId();
	}
	
	//읽기
	public HistoryDTO getHistory(Integer id) {
		Optional<History> result = historyRepository.findById(id);
		
		History history = result.orElseThrow();
		
		HistoryDTO dto = modelMapper.map(history, HistoryDTO.class);
				
		return dto;
	}
	
	//리스트
	public PageResponseDTO<HistoryDTO> getList(PageRequestDTO pageRequestDTO, Integer gamerid){
		Pageable pageable = PageRequest.of(
								pageRequestDTO.getPage() -1,
								pageRequestDTO.getSize(),
								Sort.by("id").descending());
		Page<History> result; 
		
		if(gamerid != null) {
			result = historyRepository.findByGamerId(gamerid, pageable);
		}else {
			result = historyRepository.findAll(pageable); 
		}
		
		List<HistoryDTO> dtoList = result.getContent().stream()
					.map(history -> modelMapper.map(history, HistoryDTO.class))
					.collect(Collectors.toList());
		
		Long totalCount = result.getTotalElements();
		
		PageResponseDTO<HistoryDTO> responseDTO = PageResponseDTO.<HistoryDTO>withAll()
													.dtoList(dtoList)
													.pageRequestDTO(pageRequestDTO)
													.totalCount(totalCount)
													.build();
		return responseDTO;
		
	}
	//삭제
	public void deleteHistory(Integer id) {
		historyRepository.deleteById(id);
	}
	
	//수정
	public void modifyHistory(HistoryDTO historyDTO) {
		Optional<History> result = historyRepository.findById(historyDTO.getId());
		
		History history = result.orElseThrow();
		
		history.setTitle(historyDTO.getTitle());
		history.setContent(historyDTO.getContent());
		history.setDate(historyDTO.getDate());
		history.setMate(historyDTO.getMate());
		history.setWin(historyDTO.getWin());
		history.setDraw(historyDTO.getDraw());
		history.setLose(historyDTO.getLose());
		
		
		historyRepository.save(history);
	}
	
	//최근 플레이게임 리스트
	public List<Game> getRecentGames(Integer gamerid){
		Pageable pageable = PageRequest.of(0, 10);
		return historyRepository.findRecentGamesByGamerId(gamerid, pageable);
	}
	
	//연도별 리스트
	public PageResponseDTO<HistoryDTO> getHistoryByYear(Integer gamerid, Integer year, PageRequestDTO pageRequestDTO) {
	    Pageable pageable = PageRequest.of(
	        pageRequestDTO.getPage() - 1,  // 0-based index로 변환
	        pageRequestDTO.getSize(),
	        Sort.by("id").descending() 
	    );

	    Page<History> result = historyRepository.findAllByGamerIdAndYear(gamerid, year, pageable);

	    List<HistoryDTO> dtoList = result.getContent().stream()
	        .map(history -> modelMapper.map(history, HistoryDTO.class))
	        .collect(Collectors.toList());

	    return PageResponseDTO.<HistoryDTO>withAll()
	        .dtoList(dtoList)
	        .pageRequestDTO(pageRequestDTO)
	        .totalCount(result.getTotalElements())
	        .build();
	}
	
	//전체리스트 승무패 횟수
	public Map<String, Integer> getTotalRecord(Integer gamerid, Integer year) {
	    List<Object[]> resultList;  

	    if(year != null) {
	    	//특정 연도 승무패 조회
	    	resultList = historyRepository.countWinDrawLoseByGamerIdAndYear(gamerid, year);
	    }else {
	    	//연도가 null이면 전체 기간 조회
	    	resultList = historyRepository.countWinDrawLoseByGamerId(gamerid);
	    }
	    
	    
	    //리스트에서 첫번째 결과만 가져옴
	    Object[] result = resultList.isEmpty() ? new Object[] {0,0,0} : resultList.get(0);
	    
	    int winCount = ((Number) result[0]).intValue();
	    int drawCount = ((Number) result[1]).intValue();
	    int loseCount = ((Number) result[2]).intValue();

	    return Map.of(
	        "win", winCount,
	        "draw", drawCount,
	        "lose", loseCount
	    );
	}

}
