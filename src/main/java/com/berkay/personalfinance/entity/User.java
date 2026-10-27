package com.berkay.personalfinance.entity;


import jakarta.persistence.*;
import lombok.*;
import com.berkay.personalfinance.enums.Role;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {


    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) ///Rol bilgisini veritabanına 0, 1 gibi indeks rakamı olarak değil, doğrudan "ROLE_USER" metni olarak kaydeder
    @Column(name = "role", nullable = false, length = 20)
    private Role role;


}
