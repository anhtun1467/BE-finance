package com.example.financeapp.service.impl;

import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.TransactionType;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.CategoryRepository;
import com.example.financeapp.repository.TransactionTypeRepository;
import com.example.financeapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionTypeRepository transactionTypeRepository;

    @Override
    public Category createCategory(User user, String name, String icon, Long transactionTypeId) {
        TransactionType transactionType = transactionTypeRepository.findById(transactionTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại giao dịch"));

        Category category = new Category();
        category.setCategoryName(name);
        category.setIcon(icon);
        category.setTransactionType(transactionType);
        category.setUser(user);

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, String name, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if (name != null && !name.isBlank()) {
            category.setCategoryName(name);
        }

        if (icon != null && !icon.isBlank()) {
            category.setIcon(icon);
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUser_UserId(userId);
    }
}
