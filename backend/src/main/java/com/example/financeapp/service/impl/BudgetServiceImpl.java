package com.example.financeapp.service.impl;

import com.example.financeapp.dto.CreateBudgetRequest;
import com.example.financeapp.entity.*;
import com.example.financeapp.repository.*;
import com.example.financeapp.service.BudgetService;
import com.example.financeapp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletService walletService;

    // src/main/java/com/example/financeapp/service/impl/BudgetServiceImpl.java

    @Override
    @Transactional
    public Budget createBudget(Long userId, CreateBudgetRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));

        if (!"Chi tiêu".equals(category.getTransactionType().getTypeName())) {
            throw new RuntimeException("Chỉ được tạo ngân sách cho danh mục Chi tiêu");
        }

        Wallet wallet = null;
        Long walletIdForCheck = null;
        if (request.getWalletId() != null) {
            wallet = walletRepository.findById(request.getWalletId())
                    .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

            if (!walletService.hasAccess(wallet.getWalletId(), userId)) {
                throw new RuntimeException("Bạn không có quyền truy cập ví này");
            }
            walletIdForCheck = wallet.getWalletId();
        }

        boolean alreadyExists = budgetRepository.existsExactlySameBudget(
                user,
                category,
                wallet,
                walletIdForCheck,
                request.getStartDate(),
                request.getEndDate()
        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "Bạn đã tạo ngân sách cho danh mục \"" + category.getCategoryName() +
                            "\" trong khoảng thời gian từ " + request.getStartDate() +
                            " đến " + request.getEndDate() + ". Vui lòng chỉnh sửa ngân sách cũ hoặc chọn khoảng thời gian khác."
            );
        }

        // Tạo ngân sách mới
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setWallet(wallet);
        budget.setAmountLimit(request.getAmountLimit());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setNote(request.getNote() != null && !request.getNote().trim().isEmpty()
                ? request.getNote().trim() : null);

        return budgetRepository.save(budget);
    }
}