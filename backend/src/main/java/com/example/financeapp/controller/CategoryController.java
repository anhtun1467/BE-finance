package com.example.financeapp.controller;

import com.example.financeapp.dto.CreateCategoryRequest;
import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.User;
import com.example.financeapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired private CategoryService categoryService;

    // Tạo danh mục
    @PostMapping("/create")
    public Category createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return categoryService.createCategory(
                user,
                request.getCategoryName(),
                request.getIcon(),
                request.getTransactionTypeId()
        );
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return categoryService.updateCategory(
                id,
                request.getCategoryName(),
                request.getIcon()
        );
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "Danh mục đã được xóa thành công";
    }
}
