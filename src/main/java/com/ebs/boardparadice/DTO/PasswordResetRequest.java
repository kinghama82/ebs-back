package com.ebs.boardparadice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotEmpty(message = "이름을 입력해주세요.")
    private String name;

    @NotEmpty(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;
}
