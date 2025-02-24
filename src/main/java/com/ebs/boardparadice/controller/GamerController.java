package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.service.GamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gamer")
@RequiredArgsConstructor
public class GamerController {

    private final GamerService gamerService;

    // 회원가입 엔드포인트
    @PostMapping("/register")
    public ResponseEntity<?> registerGamer(@RequestBody Gamer gamer) {
        // 이메일 중복 확인
        if (gamerService.getGamerByEmail(gamer.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("msg", "이미 등록된 이메일입니다."));
        }
        Gamer savedGamer = gamerService.registerGamer(gamer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);
    }

    // 프로필 조회 엔드포인트 (테스트용)
    @GetMapping("/profile")
    public ResponseEntity<?> getGamerProfile(@RequestParam String email) {
        GamerDTO gamerDTO = gamerService.getGamerDTOByEmail(email);
        return ResponseEntity.ok(gamerDTO.getClaims());
    }
}
