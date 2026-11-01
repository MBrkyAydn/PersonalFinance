package com.berkay.personalfinance.responsedto;

import com.berkay.personalfinance.enums.CategoryType;
import lombok.*;

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
