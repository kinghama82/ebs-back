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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/gamer")
@RequiredArgsConstructor
@Log4j2
public class GamerController {

    private final GamerService gamerService;

    // 업로드 디렉토리
    private static final String UPLOAD_DIR = "uploads/profile_images/";

    /**
     * 회원가입 (비밀번호 확인, 이메일/닉네임 중복 체크)
     * - 클라이언트에서 password1, password2를 보내고, 둘이 일치하는지 확인
     * - 이미 가입된 이메일/닉네임인지 확인
     * - 최종적으로 GamerDTO 생성 후 회원 등록
     */
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

        // GamerCreateForm → GamerDTO 변환
        GamerDTO gamerDTO = new GamerDTO(
                0, // ID는 자동 생성이므로 0
                form.getName(),
                Integer.parseInt(form.getAge()),
                form.getEmail(),
                form.getPassword1(),
                form.getNickname(),
                form.getPhone(),
                form.getAddress(),
                false, // social은 기본값 false
                LocalDateTime.now(), // 가입일자 자동 세팅
                "0", // 기본 등급 설정
                null, // profileImage 초기값 (없으면 null로 처리)
                new ArrayList<>() // 빈 역할 리스트 전달
        );


        // 회원 등록
        Gamer savedGamer = gamerService.registerGamer(gamerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGamer);
    }

    /**
     * 프로필 이미지 업로드
     * - email 파라미터와 함께 MultipartFile 업로드
     * - 업로드 후 DB의 profileImage 경로 업데이트
     */
    @PostMapping("/uploadProfile")
    public ResponseEntity<?> uploadProfileImage(@RequestParam(name = "email") String email,
                                                @RequestParam(name = "file") MultipartFile file) {
        try {
            Gamer updatedGamer = gamerService.updateProfileImage(email, file);
            return ResponseEntity.ok(Map.of(
                    "msg", "프로필 이미지가 성공적으로 업데이트되었습니다.",
                    "profileImage", updatedGamer.getProfileImage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "프로필 이미지 업데이트 실패: " + e.getMessage()));
        }
    }

//    기존코드
    /*@PostMapping("/uploadProfile")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("email") String email,
                                                @RequestParam("file") MultipartFile file) {
        Optional<Gamer> gamerOptional = gamerService.getGamerByEmail(email);
        if (gamerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "회원 정보를 찾을 수 없습니다."));
        }

        Gamer gamer = gamerOptional.get();
        try {
            // UPLOAD_DIR: 프로젝트 루트 기준으로 "src/main/resources/static/uploads/profile/"에 저장
            final String UPLOAD_DIR = "src/main/resources/static/uploads/profile/";
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 파일명 중복 방지: UUID 사용
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR, filename);
            Files.write(filepath, file.getBytes());

            // DB에 이미지 경로 업데이트
            // WebConfig에서 /uploads/** -> file:./src/main/resources/static/uploads/ 로 매핑되어 있으므로
            // 프론트에서 접근할 때는 "/uploads/profile/파일명" 으로 접근
            gamer.setProfileImage("/uploads/profile/" + filename);
            gamerService.updateGamer(gamer);

            return ResponseEntity.ok(Map.of("msg", "프로필 사진이 업로드되었습니다.", "profileImage", gamer.getProfileImage()));
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("msg", "파일 업로드 중 오류 발생"));
        }
    }*/

    /**
     * 프로필 이미지 경로 조회
     */
    @GetMapping("/profileImage")
    public ResponseEntity<?> getProfileImage(@RequestParam(name = "email") String email) {
        Optional<Gamer> gamerOptional = gamerService.getGamerByEmail(email);
        if (gamerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", "회원 정보를 찾을 수 없습니다."));
        }

        Gamer gamer = gamerOptional.get();
        return ResponseEntity.ok(Map.of("profileImage", gamer.getProfileImage()));
    }

    /**
     * 이메일 중복 체크
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam(value = "email") String email) {
        boolean exists = gamerService.getGamerByEmail(email).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * 닉네임 중복 체크
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNicknameExists(@RequestParam(value = "nickname") String nickname) {
        boolean exists = gamerService.getGamerByNickname(nickname).isPresent();
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * 회원 상세보기 (이메일로 조회)
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getGamerDetail(@RequestParam(value = "email") String email) {
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
                    gamer.getProfileImage(),
                    gamer.getGamerRoleList() != null
                            ? gamer.getGamerRoleList().stream().map(Enum::name).toList()
                            : null
            );
            return ResponseEntity.ok(gamerDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "해당 이메일의 회원을 찾을 수 없습니다."));
        }
    }

    /**
     * Refresh Token으로 Access Token 재발급
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token이 필요합니다."));
        }

        try {
            Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
            String email = (String) claims.get("email");

            // 만료 시간 10분짜리 새로운 Access Token 생성
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "유효하지 않은 Refresh Token"));
        }
    }

    /**
     * 닉네임으로 회원 찾기
     */
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Gamer> getUserByNickname(@PathVariable(name = "nickname") String nickname) {
        Gamer gamer = gamerService.findByNickname(nickname);
        if (gamer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(gamer);
    }
}
