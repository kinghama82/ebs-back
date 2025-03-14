package com.ebs.boardparadice.DTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.ebs.boardparadice.controller.formatter.LocalDateFormatter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자
public class GamerDTO implements UserDetails { // 🔹 User 대신 UserDetails 직접 구현

    private Integer id;
    private String name;
    private Integer age;
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String address;
    private boolean social;
    private LocalDateTime createdate;
    private String level;
    private String profileImage;
    private List<String> roleNames = new ArrayList<>();

    public GamerDTO(Integer id, String name, Integer age, String email, String password, String nickname,
                    String phone, String address, boolean social, LocalDateTime createdate, String level,
                    String profileImage, List<String> roleNames) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.social = social;
        this.createdate = createdate;
        this.level = level;
        this.profileImage = profileImage;
        this.roleNames = roleNames != null ? roleNames : new ArrayList<>();
    }

    // 🔹 JWT 생성에 필요한 데이터를 Map으로 변환하는 메서드 추가
    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", id);
        dataMap.put("name", name);
        dataMap.put("age", age);
        dataMap.put("email", email);
        dataMap.put("nickname", nickname);
        dataMap.put("phone", phone);
        dataMap.put("address", address);
        dataMap.put("social", social);
        dataMap.put("profileImage", profileImage);
        dataMap.put("level", level);
        dataMap.put("roleNames", roleNames);
        // LocalDateTime를 LocalDate로 변환한 후 LocalDateFormatter로 문자열로 변환
        if (createdate != null) {
            LocalDateFormatter formatter = new LocalDateFormatter();
            dataMap.put("createdate", formatter.print(createdate.toLocalDate(), Locale.getDefault()));
        } else {
            dataMap.put("createdate", null);
        }

        return dataMap;
    }

    // 🔹 UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleNames.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
