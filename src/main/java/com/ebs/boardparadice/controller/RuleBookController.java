package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.PageRequestDTO;
import com.ebs.boardparadice.DTO.PageResponseDTO;
import com.ebs.boardparadice.DTO.RulebookDTO;
import com.ebs.boardparadice.service.RulebookService;
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
    public PageResponseDTO<RulebookDTO> List(PageRequestDTO pageRequestDTO){

        return rulebookService.getList(pageRequestDTO);
    }

    @GetMapping("/{id}")
    public RulebookDTO read(@PathVariable(name="id") Integer id){
        return rulebookService.get(id);
    }

    @PutMapping("/{id}")
    public Map<String, String> modify(@PathVariable(name="id") Integer id, RulebookDTO rulebookDTO){

        rulebookDTO.setId(id);

        rulebookService.modify(rulebookDTO);
        return Map.of("result", "성공");

    }

    @DeleteMapping("/{id}")
    public Map<String, String> remove(@PathVariable(name="id") Integer id){

        rulebookService.remove(id);
        return Map.of("결과", "성공");
    }


}
