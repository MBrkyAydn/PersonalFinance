package com.berkay.personalfinance.entity;


import com.berkay.personalfinance.enums.CategoryType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CategoryType type;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

}
