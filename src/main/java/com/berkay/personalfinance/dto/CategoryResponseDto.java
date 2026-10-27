package com.berkay.personalfinance.dto;

import com.berkay.personalfinance.enums.CategoryType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDto {
private  Long id;
private  String name;
private CategoryType categoryType;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;

}
