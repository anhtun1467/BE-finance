package com.example.financeapp.service;

import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.TransactionType;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.CategoryRepository;
import com.example.financeapp.repository.TransactionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionTypeRepository transactionTypeRepository;

    // Tạo danh mục (dùng trực tiếp User từ token)
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

    public abstract Category updateCategory(Long id, String name, String icon);

    public abstract void deleteCategory(Long id);

    public abstract List<Category> getCategoriesByUser(Long userId);
}
