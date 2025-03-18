package com.ebs.boardparadice.service;

import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.model.GamerRole;
import com.ebs.boardparadice.repository.GamerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ebs.boardparadice.config.WebConfig;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamerService {

    private final GamerRepository gamerRepository;
    private final PasswordEncoder passwordEncoder;

    // 📂 업로드 디렉토리 경로 (절대 경로 사용 가능)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/profile/";
    // 메모리 기반 비밀번호 재설정 토큰 저장소 (실제 서비스에서는 만료 시간과 보안을 고려해야 함)
    private Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();

    // 회원가입: 비밀번호 암호화, 기본 역할 부여 후 저장
    public Gamer registerGamer(GamerDTO gamerDTO) {
        Gamer gamer = Gamer.builder()
                .name(gamerDTO.getName())
                .age(gamerDTO.getAge())
                .email(gamerDTO.getEmail())
                .password(passwordEncoder.encode(gamerDTO.getPassword()))
                .nickname(gamerDTO.getNickname())
                .phone(gamerDTO.getPhone())
                .address(gamerDTO.getAddress())
                .social(gamerDTO.isSocial())
                .level(gamerDTO.getLevel())
                .build();

        gamer.addRole(GamerRole.USER);

        if (gamer.getNickname() != null && gamer.getNickname().toLowerCase().startsWith("admin")) {
            gamer.addRole(GamerRole.ADMIN);
        }

        return gamerRepository.save(gamer);
    }

    @Transactional(readOnly = true)
    public GamerDTO getGamerDTOByEmail(String email) {
        Gamer gamer = gamerRepository.getWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + email));

        List<String> roleNames = Optional.ofNullable(gamer.getGamerRoleList())
                .orElse(new ArrayList<>())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return new GamerDTO(
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
                roleNames
        );
    }

    public Optional<Gamer> getGamerByEmail(String email) {
        return gamerRepository.findByEmail(email);
    }

    public Optional<Gamer> getGamerByNickname(String nickname) {
        return gamerRepository.findByNickname(nickname);
    }

    public Gamer getGamerById(int id) {
        return gamerRepository.findById(id).orElse(null);
    }

    public Gamer findByNickname(String nickname) {
        return gamerRepository.findByNickname(nickname).orElse(null);
    }

    /**
     * Gamer 정보 업데이트 (프로필 이미지 포함)
     */
    @Transactional
    public Gamer updateGamer(Gamer gamer) {
        return gamerRepository.save(gamer);
    }

    @Transactional
    public Gamer updateProfileImage(String email, String imagePath) {
        Gamer gamer = gamerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + email));

        // 기존 프로필 이미지 삭제 (필요 시)
        if (gamer.getProfileImage() != null && !gamer.getProfileImage().isEmpty()) {
            String existingFileName = gamer.getProfileImage().substring("/uploads/profile/".length());
            // WebConfig에 정의된 업로드 기본 경로를 사용
            Path existingFilePath = Paths.get(WebConfig.UPLOAD_BASE_PATH, "profile", existingFileName);
            File existingFile = existingFilePath.toFile();
            if (existingFile.exists()) {
                try {
                    Files.delete(existingFilePath);
                    System.out.println("✅ 기존 프로필 이미지 삭제됨: " + existingFilePath.toString());
                } catch (Exception e) {
                    System.err.println("🚨 기존 프로필 이미지 삭제 실패: " + e.getMessage());
                }
            }
        }

        // 새 이미지 경로 업데이트
        gamer.setProfileImage(imagePath);
        return gamerRepository.save(gamer);
    }

    public List<Gamer> searchGamersByNickname(String nickname) {
        return gamerRepository.searchByNickname(nickname);
    }

    //    비밀번호 변경
    @Transactional
    public Gamer changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        Gamer gamer = gamerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, gamer.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 검증
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 업데이트
        gamer.setPassword(passwordEncoder.encode(newPassword));
        return gamerRepository.save(gamer);
    }

    public Optional<Gamer> findByNameAndPhone (String name, String phone) {
        return gamerRepository.findByNameAndPhone(name, phone);
    }

    public String createPasswordResetToken(String email) {
        // 사용자 확인 후 토큰 생성 (예: UUID 사용)
        String token = UUID.randomUUID().toString();
        // 토큰과 이메일을 저장 (토큰을 key로 하고, 이메일을 value로 저장)
        passwordResetTokens.put(token, email);
        return token;
    }


    @Transactional
    public Gamer resetPassword(String token, String newPassword, String confirmPassword) {
        // 토큰 유효성 확인 (토큰이 DB나 캐시에 있는지, 만료되지 않았는지 확인)
        // 예시: String email = passwordResetTokens.get(token);
        String email = validateAndRetrieveEmailFromToken(token); // 직접 구현 필요
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }
        Gamer gamer = gamerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 회원을 찾을 수 없습니다."));
        gamer.setPassword(passwordEncoder.encode(newPassword));
        // 토큰 사용 완료 후 삭제 처리
        removePasswordResetToken(token);
        return gamerRepository.save(gamer);
    }

    // 토큰의 유효성을 검사하고 해당 이메일을 반환 (유효하지 않으면 null)
    private String validateAndRetrieveEmailFromToken(String token) {
        return passwordResetTokens.get(token);
    }

    // 사용 완료된 토큰 삭제
    private void removePasswordResetToken(String token) {
        passwordResetTokens.remove(token);
    }

}