package com.example.financeapp.repository;

import com.example.financeapp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // <-- Cần thiết cho việc tìm kiếm chi tiết 1 đối tượng

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Tìm danh sách ví theo User ID (cho getMyWallets)
    List<Wallet> findByUser_UserId(Long userId);

    // Kiểm tra trùng tên ví trong phạm vi User (cho createWallet)
    boolean existsByWalletNameAndUser_UserId(String walletName, Long userId);

    Optional<Wallet> findByWalletIdAndUser_UserId(Long walletId, Long userId);
}