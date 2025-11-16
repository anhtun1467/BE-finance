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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    // ==============================
    // TẠO DANH MỤC
    // ==============================
    @Override
    public Category createCategory(User user, String name, String icon, Long transactionTypeId) {
        TransactionType type = transactionTypeRepository.findById(transactionTypeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại giao dịch"));

        // Kiểm tra trùng tên: cùng tên + cùng loại + (của user HOẶC hệ thống)
        boolean duplicate = categoryRepository.existsByCategoryNameAndTransactionTypeAndUser(name, type, user)
                || categoryRepository.existsByCategoryNameAndTransactionTypeAndUserIsNullAndIsSystemTrue(name, type);

        if (duplicate) {
            throw new RuntimeException("Danh mục '" + name + "' đã tồn tại trong loại giao dịch này");
        }

        Category category = new Category(name, type, icon, user, false); // false = user-created
        return categoryRepository.save(category);
    }

    // ==============================
    // CẬP NHẬT DANH MỤC
    // ==============================
    @Override
    public Category updateCategory(User currentUser, Long id, String name, String icon) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        // Không cho sửa danh mục hệ thống
        if (category.isSystem()) {
            throw new RuntimeException("Không thể sửa danh mục hệ thống");
        }

        // Chỉ cho phép sửa danh mục của chính mình
        if (category.getUser() == null || !category.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không có quyền sửa danh mục này");
        }

        // Nếu đổi tên → kiểm tra trùng
        if (name != null && !name.isBlank() && !name.equals(category.getCategoryName())) {
            boolean duplicate = categoryRepository.existsByCategoryNameAndTransactionTypeAndUser(name, category.getTransactionType(), currentUser)
                    || categoryRepository.existsByCategoryNameAndTransactionTypeAndUserIsNullAndIsSystemTrue(name, category.getTransactionType());

            if (duplicate) {
                throw new RuntimeException("Danh mục '" + name + "' đã tồn tại trong loại giao dịch này");
            }
            category.setCategoryName(name);
        }

        if (icon != null && !icon.isBlank()) {
            category.setIcon(icon);
        }

        return categoryRepository.save(category);
    }

    // ==============================
    // XÓA DANH MỤC
    // ==============================
    @Override
    public void deleteCategory(User currentUser, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if (category.isSystem()) {
            throw new RuntimeException("Không thể xóa danh mục hệ thống");
        }

        if (category.getUser() == null || !category.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Bạn không có quyền xóa danh mục này");
        }

        categoryRepository.delete(category);
    }

    // ==============================
    // LẤY DANH SÁCH DANH MỤC CỦA USER
    // ==============================
    @Override
    public List<Category> getCategoriesByUser(User user) {
        List<Category> systemCategories = categoryRepository.findByUserIsNullAndIsSystemTrue();
        List<Category> userCategories = categoryRepository.findByUser(user);

        return Stream.concat(systemCategories.stream(), userCategories.stream())
                .collect(Collectors.toList());
    }
}