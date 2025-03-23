// src/main/java/com/ebs/boardparadice/DTO/ChangePasswordRequest.java
package com.ebs.boardparadice.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;

    @NotEmpty(message = "현재 비밀번호를 입력하세요.")
    private String currentPassword;

    @NotEmpty(message = "새 비밀번호를 입력하세요.")
    private String newPassword;

    @NotEmpty(message = "새 비밀번호 확인을 입력하세요.")
    private String confirmPassword;
}
