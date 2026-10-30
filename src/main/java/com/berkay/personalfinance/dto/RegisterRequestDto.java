package com.berkay.personalfinance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder

public class RegisterRequestDto {

    @NotBlank(message = "E-posta adresi boş bırakılamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;
    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, max = 20, message = "Şifre en az 6, en fazla 20 karakter olmalı")
    private String password;

}
