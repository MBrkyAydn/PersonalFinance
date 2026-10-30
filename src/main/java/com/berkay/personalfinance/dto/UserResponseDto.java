package com.berkay.personalfinance.dto;

import com.berkay.personalfinance.enums.Role;
import lombok.*;

 import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder

public class UserResponseDto {
    private Long id;
    private String email;
    private Role role;
    private LocalDateTime createdDate;


}
