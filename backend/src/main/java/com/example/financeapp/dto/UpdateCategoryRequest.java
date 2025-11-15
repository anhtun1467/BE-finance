package com.example.financeapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryRequest {

    private String categoryName; // tên mới
    private String icon;         // icon mới
}
