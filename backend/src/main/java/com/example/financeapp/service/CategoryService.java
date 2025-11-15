package com.example.financeapp.service;

import com.example.financeapp.entity.Category;
import com.example.financeapp.entity.User;

import java.util.List;

public interface CategoryService {

    Category createCategory(User user, String name, String icon, Long transactionTypeId);

    Category updateCategory(User currentUser, Long id, String name, String icon);

    void deleteCategory(User currentUser, Long id);

    List<Category> getCategoriesByUser(User user);
}