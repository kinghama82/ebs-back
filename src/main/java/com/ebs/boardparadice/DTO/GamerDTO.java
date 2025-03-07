package com.ebs.boardparadice.DTO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.ebs.boardparadice.controller.formatter.LocalDateFormatter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GamerDTO extends User {

    private int id;
    private String name;
    private int age;
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private String address;
    private boolean social;
    private LocalDateTime createdate;
    private String level;

    private List<String> roleNames = new ArrayList<>();

    public GamerDTO(int id, String name, int age, String email, String password, String nickname, String phone, String address, boolean social, LocalDateTime createdate, String level, List<String> roleNames) {
        super(email, password,
                (roleNames != null ? roleNames : new ArrayList<>())
                        .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()));
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
        this.roleNames = roleNames != null ? roleNames : new ArrayList<>();
    }


    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", id);
        dataMap.put("name", name);
        dataMap.put("age", age);
        dataMap.put("email", email);
        dataMap.put("password", password);
        dataMap.put("nickname", nickname);
        dataMap.put("phone", phone);
        dataMap.put("address", address);
        dataMap.put("social", social);
        // LocalDateTime를 LocalDate로 변환한 후 LocalDateFormatter로 문자열로 변환
        if (createdate != null) {
            LocalDateFormatter formatter = new LocalDateFormatter();
            dataMap.put("createdate", formatter.print(createdate.toLocalDate(), Locale.getDefault()));
        } else {
            dataMap.put("createdate", null);
        }
        dataMap.put("level", level);
        dataMap.put("roleNames", roleNames);
        return dataMap;
    }

}
