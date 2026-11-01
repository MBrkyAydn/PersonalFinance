package com.berkay.personalfinance.requestdto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "E-posta alanı boş bırakılamaz.")
    @Email(message = "Lütfen geçerli bir e-posta giriniz.")
    private String email;

    @NotBlank(message = "Şifre alanı boş bırakılamaz.")
    private String password;

}
