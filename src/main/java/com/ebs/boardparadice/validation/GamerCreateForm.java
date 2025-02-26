package com.ebs.boardparadice.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GamerCreateForm {

    @Size(min = 2, max = 50, message = "이름은 2자 이상 입력해주세요.")
    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "나이는 필수 항목입니다.")
    private String age;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
            message = "비밀번호는 알파벳과 숫자를 포함해야 합니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "닉네임은 필수 항목입니다.")
    private String nickname;

    @NotEmpty(message = "전화번호는 필수 항목입니다.")
    @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4})$", message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;

    @Size(max = 100, message = "주소는 최대 100자까지 입력 가능합니다.")
    private String address;
}
