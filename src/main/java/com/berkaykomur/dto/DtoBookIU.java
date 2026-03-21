package com.berkaykomur.dto;

import com.berkaykomur.model.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoBookIU {
    @Size(max = 200, message = "Kitap adı 200 karakterden uzun olamaz")
    private String title;
    
    @NotBlank(message = "kitap yazarı boş olamaz")
    @Size(max = 100, message = "Yazar adı 100 karakterden uzun olamaz")
    private String author;
    
    @NotNull(message = "Yanlış ya da girmeyi unuttun")
    private Category category;

    @NotBlank(message = "ISBN boş olamaz")
    @Size(min=13,max=13,message = "ISBN 13 haneli olmalıdır")
    private String isbnNo;
    
    private boolean available;
}