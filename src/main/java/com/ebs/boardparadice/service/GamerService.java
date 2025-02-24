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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamerService {

    private final GamerRepository gamerRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입: 비밀번호 암호화, 기본 역할 부여 후 저장
    public Gamer registerGamer(Gamer gamer) {
        gamer.setPassword(passwordEncoder.encode(gamer.getPassword()));
        gamer.addRole(GamerRole.USER);
        if (gamer.getNickname() != null && gamer.getNickname().toLowerCase().startsWith("admin")) {
            gamer.addRole(GamerRole.ADMIN);
        }
        return gamerRepository.save(gamer);
    }

    // DTO로 변환하는 메서드에 @Transactional을 적용하여 세션을 유지한 상태에서 변환 수행
    @Transactional(readOnly = true)
    public GamerDTO getGamerDTOByEmail(String email) {
        // EntityGraph를 활용하여 역할 정보를 미리 로드 (Repository에서 정의한 메서드)
        Gamer gamer = gamerRepository.getWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + email));

        // gamerRoleList에 접근 시 이미 로드되어 있음
        List<String> roleNames = gamer.getGamerRoleList()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        // 엔티티의 데이터를 기반으로 DTO 생성
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
                roleNames
        );
    }

    public Optional<Gamer> getGamerByEmail(String email) {
        return gamerRepository.findByEmail(email);
    }

    public  Gamer getGamerById(int id) {
        return gamerRepository.findById(id).orElse(null);
    }
}
