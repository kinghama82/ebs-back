package com.ebs.boardparadice.controller.boards;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.boards.NewsDTO;
import com.ebs.boardparadice.DTO.boards.RulebookDTO;
import com.ebs.boardparadice.service.boards.RulebookService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rulebook")
public class RuleBookController {


    private final RulebookService rulebookService;


    @GetMapping("/list")
    public PageResponseDTO<RulebookDTO> list(PageRequestDTO pageRequestDTO){

        return rulebookService.getList(pageRequestDTO);
    }

    @GetMapping("/{id}")
    public RulebookDTO get(@PathVariable(name="id") Integer id){
        return rulebookService.getRulebook(id);
    }

    @PostMapping("/")
    public Map<String, Integer> create(@RequestBody RulebookDTO rulebookDTO){

        Integer id = rulebookService.createRulebook(rulebookDTO);
        return Map.of("id", id);
    }

    @PutMapping("/{id}")
    public Map<String, String> modify(@PathVariable(name="id") Integer id, RulebookDTO rulebookDTO){

        rulebookDTO.setId(id);

        rulebookService.modifyRulebook(rulebookDTO);
        return Map.of("result", "성공");

    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable(name="id") Integer id){

        rulebookService.deleteRulebook(id);
        return Map.of("결과", "성공");
    }


}
