// ResetPasswordDTO.java
package com.ebs.boardparadice.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotEmpty(message = "토큰은 필수입니다.")
    private String token;

    @NotEmpty(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;

    @NotEmpty(message = "새 비밀번호 확인을 입력해주세요.")
    private String confirmPassword;
}
