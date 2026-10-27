package com.berkay.personalfinance.dto;


import com.berkay.personalfinance.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "Kategori adı boş olamaz")
    @Size(min = 2,max = 50,message = "Kategori adı 2 ile 50 karakter arasında olmalıdır")
    private String name;

    @NotNull(message = "Kategori türü (INCOME veya EXPENSE) belirtilmelidir")
    private CategoryType categoryType;


}
