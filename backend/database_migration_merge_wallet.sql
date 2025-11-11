-- ============================================
-- MIGRATION: WALLET MERGE FEATURE
-- Tạo bảng wallet_merge_history để lưu lịch sử gộp ví
-- ============================================

-- Tạo bảng wallet_merge_history
CREATE TABLE IF NOT EXISTS wallet_merge_history (
    merge_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- User info
    user_id BIGINT NOT NULL COMMENT 'User thực hiện merge',
    
    -- Source wallet info (ví bị xóa)
    source_wallet_id BIGINT NOT NULL COMMENT 'ID ví nguồn',
    source_wallet_name VARCHAR(100) NOT NULL COMMENT 'Tên ví nguồn',
    source_currency VARCHAR(3) NOT NULL COMMENT 'Loại tiền ví nguồn',
    source_balance DECIMAL(15,2) NOT NULL COMMENT 'Số dư ví nguồn',
    source_transaction_count INT NOT NULL DEFAULT 0 COMMENT 'Số giao dịch ví nguồn',
    
    -- Target wallet info (ví đích - giữ lại)
    target_wallet_id BIGINT NOT NULL COMMENT 'ID ví đích',
    target_wallet_name VARCHAR(100) NOT NULL COMMENT 'Tên ví đích',
    target_currency VARCHAR(3) NOT NULL COMMENT 'Loại tiền ví đích',
    target_balance_before DECIMAL(15,2) NOT NULL COMMENT 'Số dư ví đích trước merge',
    target_balance_after DECIMAL(15,2) NOT NULL COMMENT 'Số dư ví đích sau merge',
    target_transaction_count_before INT NOT NULL DEFAULT 0 COMMENT 'Số giao dịch ví đích trước merge',
    
    -- Metadata
    merged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm merge',
    merge_duration_ms BIGINT NULL COMMENT 'Thời gian thực hiện (milliseconds)',
    
    -- Foreign key
    CONSTRAINT fk_merge_history_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id) 
        ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_user_merged_at (user_id, merged_at),
    INDEX idx_target_wallet (target_wallet_id),
    INDEX idx_source_wallet (source_wallet_id),
    INDEX idx_merged_at (merged_at)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Lưu lịch sử gộp ví';

-- ============================================
-- VERIFY TABLE STRUCTURE
-- ============================================

SHOW CREATE TABLE wallet_merge_history;

-- ============================================
-- SAMPLE QUERIES
-- ============================================

-- Lấy lịch sử merge của user (10 lần gần nhất)
-- SELECT * FROM wallet_merge_history 
-- WHERE user_id = ? 
-- ORDER BY merged_at DESC 
-- LIMIT 10;

-- Tìm tất cả merges liên quan đến một wallet
-- SELECT * FROM wallet_merge_history 
-- WHERE source_wallet_id = ? OR target_wallet_id = ?
-- ORDER BY merged_at DESC;

-- Statistics: Tổng số tiền đã merge
-- SELECT 
--     target_currency,
--     SUM(source_balance) as total_merged_amount,
--     COUNT(*) as merge_count
-- FROM wallet_merge_history
-- WHERE user_id = ?
-- GROUP BY target_currency;

-- ============================================
-- ROLLBACK SCRIPT (nếu cần)
-- ============================================

-- DROP TABLE IF EXISTS wallet_merge_history;

-- ============================================
-- NOTES
-- ============================================
-- 1. Bảng này chỉ lưu lịch sử, không ảnh hưởng business logic
-- 2. ON DELETE CASCADE: Khi user bị xóa → history cũng xóa
-- 3. Không có FK đến wallets vì source wallet đã bị xóa
-- 4. Dùng cho audit trail và analytics

