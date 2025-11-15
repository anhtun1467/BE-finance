package com.example.financeapp.repository;

import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.TransactionType;
import com.example.financeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Lấy danh mục theo User object
    List<Category> findByUser(User user);

    // Lấy danh mục theo User và loại giao dịch
    List<Category> findByUserAndTransactionType(User user, TransactionType type);

    // Lấy danh mục theo userId
    List<Category> findByUser_UserId(Long userId);
}
