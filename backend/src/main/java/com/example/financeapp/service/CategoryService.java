package com.example.financeapp.service;

import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.TransactionType;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.CategoryRepository;
import com.example.financeapp.repository.TransactionTypeRepository;
import com.example.financeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TransactionTypeRepository transactionTypeRepository;
    @Autowired private UserRepository userRepository;

    // Tạo danh mục (giữ nguyên)
    public Category createCategory(Long userId, String name, String icon, Long transactionTypeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        TransactionType transactionType = transactionTypeRepository.findById(transactionTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại giao dịch"));

        Category category = new Category();
        category.setCategoryName(name);
        category.setIcon(icon);
        category.setTransactionType(transactionType);
        category.setUser(user);

        return categoryRepository.save(category);
    }

    // KHÔNG được đặt method abstract bên trong method khác → sửa ra ngoài
    public abstract Category updateCategory(Long id, String name, String icon);

    public abstract void deleteCategory(Long id);

    public abstract List<Category> getCategoriesByUser(Long userId);

}
