package com.banson.healthtagram.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    @Email
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String checkPassword;

    @NotEmpty
    private String nickname;

    @NotEmpty
    private String name;

}
