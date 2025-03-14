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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamerService {

    private final GamerRepository gamerRepository;
    private final PasswordEncoder passwordEncoder;

    // 📂 업로드 디렉토리 경로 (절대 경로 사용 가능)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/profile/";

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

    // 프로필 이미지 업데이트 로직
    @Transactional
    public Gamer updateProfileImage(String email, MultipartFile file) throws Exception {
        Gamer gamer = gamerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다: " + email));

        // 1️⃣ 기존 프로필 이미지 삭제
        if (gamer.getProfileImage() != null && !gamer.getProfileImage().isEmpty()) {
            String existingFilePath = UPLOAD_DIR + gamer.getProfileImage().substring("/uploads/profile/".length());
            Path path = Paths.get(existingFilePath);
            File existingFile = path.toFile();
            if (existingFile.exists()) {
                Files.delete(path);
                System.out.println("✅ 기존 프로필 이미지 삭제됨: " + existingFilePath);
            }
        }

        // 2️⃣ 새 이미지 저장
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // 디렉토리가 없으면 생성
        }

        String newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + newFileName);
        Files.copy(file.getInputStream(), filePath);

        // 3️⃣ 새 이미지 경로를 DB에 저장
        String profileImageUrl = "/uploads/profile/" + newFileName;
        gamer.setProfileImage(profileImageUrl);
        return gamerRepository.save(gamer);
    }
}
