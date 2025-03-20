package com.ebs.boardparadice.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ebs.boardparadice.DTO.GamerDTO;
import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.repository.GamerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final GamerRepository gamerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("LoadUserByUsername : " + username);

        // 이메일을 통해 Gamer 엔티티 조회
        Gamer gamer = gamerRepository.getWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + username));

        // 역할 정보 추출
        List<String> roleNames = gamer.getGamerRoleList()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        // 🔹 GamerDTO를 생성하여 반환 (UserDetails 구현체로 사용 가능)
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
}
