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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamerService {

    private final GamerRepository gamerRepository;
    private final PasswordEncoder passwordEncoder;

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
}
