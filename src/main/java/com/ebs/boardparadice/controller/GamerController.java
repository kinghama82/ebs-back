package com.ebs.boardparadice.controller;

import com.ebs.boardparadice.DTO.*;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.service.EmailService;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.ebs.boardparadice.config.WebConfig;


@RestController
@RequestMapping("/api/gamer")
@RequiredArgsConstructor
@Log4j2
public class GamerController {

    private final GamerService gamerService;
    private final EmailService emailService;

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
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("email") String email,
            @RequestParam("file") MultipartFile file) {
        try {
            // 프로필 사진 저장 (helper 메서드 호출)
            String imagePath = saveProfileImage(file);

            // 저장된 이미지 URL을 사용해 DB 업데이트 (서비스 메서드는 String을 기대함)
            Gamer updatedGamer = gamerService.updateProfileImage(email, imagePath);

            return ResponseEntity.ok(Map.of(
                    "msg", "프로필 이미지가 성공적으로 업데이트되었습니다.",
                    "profileImage", updatedGamer.getProfileImage()
            ));
        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "프로필 이미지 업데이트 실패: " + e.getMessage()));
        }
    }

    /*public ResponseEntity<?> uploadProfileImage(@RequestParam(name = "email") String email,
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
    }*/

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
     * 회원 정보 업데이트 (예: 닉네임, 전화번호, 주소 등 변경)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateGamer(@RequestBody GamerDTO gamerDTO) {
        // 기존 회원 정보가 존재하는지 확인
        Optional<Gamer> optionalGamer = gamerService.getGamerByEmail(gamerDTO.getEmail());
        if (optionalGamer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "해당 이메일의 회원을 찾을 수 없습니다."));
        }
        Gamer gamer = optionalGamer.get();

        // 업데이트할 필드를 설정 (여기서는 닉네임, 전화번호, 주소를 예시로 함)
        gamer.setNickname(gamerDTO.getNickname());
        gamer.setPhone(gamerDTO.getPhone());
        gamer.setAddress(gamerDTO.getAddress());
        // 필요하다면 다른 필드도 업데이트

        Gamer updatedGamer = gamerService.updateGamer(gamer);
        return ResponseEntity.ok(updatedGamer);
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<GamerDTO>> searchGamers(@RequestParam String nickname) {
        List<Gamer> gamers = gamerService.searchGamersByNickname(nickname);
        List<GamerDTO> result = gamers.stream()
                .map(g -> new GamerDTO(
                        g.getId(),
                        g.getName(),
                        g.getAge(),
                        g.getEmail(),
                        g.getPassword(),
                        g.getNickname(),
                        g.getPhone(),
                        g.getAddress(),
                        g.isSocial(),
                        g.getCreatedate(),
                        g.getLevel(),
                        g.getProfileImage(),
                        g.getGamerRoleList() != null ? g.getGamerRoleList().stream().map(Enum::name).toList() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 비밀번호 변경
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Gamer updatedGamer = gamerService.changePassword(
                    request.getEmail(),
                    request.getCurrentPassword(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );
            return ResponseEntity.ok(Map.of("msg", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("msg", "비밀번호 변경에 실패하였습니다."));
        }
    }


    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdRequest request) {
        // 이름과 전화번호로 회원 조회
        Optional<Gamer> optionalGamer = gamerService.findByNameAndPhone(request.getName(), request.getPhone());
        if (optionalGamer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "일치하는 회원 정보를 찾을 수 없습니다."));
        }
        Gamer gamer = optionalGamer.get();

        // 이메일 정보를 직접 응답에 포함시켜 반환
        return ResponseEntity.ok(Map.of("msg", "당신의 아이디는 :", "email", gamer.getEmail()));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        // 이메일로 회원 조회 후 이름이 일치하는지 확인 (이름과 이메일로 식별)
        Optional<Gamer> optionalGamer = gamerService.getGamerByEmail(request.getEmail());
        if (optionalGamer.isEmpty() ||
                !optionalGamer.get().getName().equalsIgnoreCase(request.getName())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "입력하신 정보와 일치하는 회원을 찾을 수 없습니다."));
        }
        Gamer gamer = optionalGamer.get();

        // 비밀번호 재설정 토큰 생성 및 저장
        String token = gamerService.createPasswordResetToken(gamer.getEmail());

        // 프론트엔드의 비밀번호 재설정 페이지 URL (토큰을 파라미터로 전달)
        String resetUrl = "https://boardparadice.com/reset-password?token=" + token;
        String subject = "비밀번호 재설정 요청";
        String content = "안녕하세요, " + gamer.getName() + "님.\n\n"
                + "입력하신 정보와 일치하는 회원이 확인되었습니다.\n"
                + "아래 링크를 클릭하여 비밀번호를 재설정해주세요:\n" + resetUrl + "\n\n"
                + "만약 본인이 요청하지 않으셨다면 이 이메일을 무시하세요.";

        // 이메일 전송 (EmailService를 통해)
        emailService.sendSimpleMessage(gamer.getEmail(), subject, content);

        return ResponseEntity.ok(Map.of("msg", "비밀번호 재설정 링크가 해당 이메일로 전송되었습니다."));
    }


    // GamerController.java - 비밀번호 재설정 엔드포인트
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        try {
            Gamer updatedGamer = gamerService.resetPassword(dto.getToken(), dto.getNewPassword(), dto.getConfirmPassword());
            return ResponseEntity.ok(Map.of("msg", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("msg", "비밀번호 재설정에 실패하였습니다."));
        }
    }

    private String saveProfileImage(MultipartFile imgFile) throws Exception {
        // 운영 환경: baseUploadPath는 "/home/ubuntu/uploads"가 됨.
        Path uploadPath = Paths.get(WebConfig.UPLOAD_BASE_PATH, "profile");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ 프로필 업로드 디렉토리 생성됨: " + uploadPath.toString());
        }

        String originalFileName = imgFile.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("\\s+", "");
        Path filePath = uploadPath.resolve(fileName);
        System.out.println("🟢 프로필 파일 저장 시도 중: " + filePath.toString());

        try {
            Files.copy(imgFile.getInputStream(), filePath);
            System.out.println("✅ 프로필 파일 저장 완료: " + filePath.toString());
        } catch (Exception e) {
            System.err.println("🚨 프로필 파일 저장 오류: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // 웹 접근 경로 반환 (WebConfig와 일치)
        return "/uploads/profile/" + fileName;
    }

}