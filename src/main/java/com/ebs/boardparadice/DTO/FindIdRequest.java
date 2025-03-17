// FindIdRequest.java
package com.ebs.boardparadice.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindIdRequest {
    @NotEmpty(message = "이름을 입력해주세요.")
    private String name;

    @NotEmpty(message = "전화번호를 입력해주세요.")
    private String phone;
}
