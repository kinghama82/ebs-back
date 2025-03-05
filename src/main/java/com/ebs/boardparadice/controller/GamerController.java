package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.service.GamerService;
import com.ebs.boardparadice.util.CustomJWTException;
import com.ebs.boardparadice.util.JWTUtil;
import com.ebs.boardparadice.validation.GamerCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/gamer")
@RequiredArgsConstructor
@Log4j2
public class GamerController {

    private final GamerService gamerService;

    @PostMapping("/new")
    public ResponseEntity<?> registerGamer(@Valid @RequestBody GamerCreateForm form) {
        // 비밀번호 일치 확인
        if (!form.getPassword1().equals(form.getPassword2())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", "비밀번호가 일치하지 않습니다."));
        }

        // 이메일 중복 확인
        if (gamerService.getGamerByEmail(form.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("msg", "이미 등록된 이메일입니다."));
        }

        // 닉네임 중복 확인
        if (gamerService.getGamerByNickname(form.getNickname()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("msg", "이미 사용 중인 닉네임입니다."));
        }

        // GamerCreateForm을 GamerDTO로 변환
        GamerDTO gamerDTO = new GamerDTO(
                0, // ID는 자동 생성이므로 0 또는 기본값 사용
                form.getName(),
                Integer.parseInt(form.getAge()),
                form.getEmail(),
                form.getPassword1(),
                form.getNickname(),
                form.getPhone(),
                form.getAddress(),
                false, // social은 기본값 false
                null, // createdate는 엔티티에서 자동 설정
                "0", // 기본 등급 설정
                new ArrayList<>() // 빈 역할 리스트 전달
        );


        // DTO를 서비스에 전달하여 등록
        Gamer savedGamer = gamerService.registerGamer(gamerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);
    }
    // 회원 상세보기 (이메일을 통한 조회)
    @GetMapping("/detail")
    public ResponseEntity<?> getGamerDetail(@RequestParam String email) {
        Optional<Gamer> gamerOptional = gamerService.getGamerByEmail(email);

        if (gamerOptional.isPresent()) {
            Gamer gamer = gamerOptional.get();
            GamerDTO gamerDTO = new GamerDTO(
                    gamer.getId(),
                    gamer.getName(),
                    gamer.getAge(),
                    gamer.getEmail(),
                    gamer.getPassword(),
                    gamer.getNickname(),
                    gamer.getPhone(),
                    gamer.getAddress(),
                    gamer.isSocial(),
                    gamer.getCreatedate(),
                    gamer.getLevel(),
                    gamer.getGamerRoleList() != null ?
                            gamer.getGamerRoleList().stream().map(Enum::name).toList() : null
            );
            return ResponseEntity.ok(gamerDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "해당 이메일의 회원을 찾을 수 없습니다."));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token이 필요합니다."));
        }

        try {
            Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
            String email = (String) claims.get("email");

            String newAccessToken = JWTUtil.generateToken(Map.of(
                    "email", email,
                    "roleNames", claims.get("roleNames")
            ), 10);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", refreshToken
            ));
        } catch (CustomJWTException e) {
            log.error("Refresh Token 검증 실패: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 Refresh Token"));
        }
    }


}






