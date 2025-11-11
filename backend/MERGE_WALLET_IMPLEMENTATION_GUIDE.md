# 🚀 HƯỚNG DẪN TRIỂN KHAI CHỨC NĂNG GỘP VÍ

## ✅ ĐÃ HOÀN THÀNH

Tính năng gộp ví đã được triển khai đầy đủ theo **OPTION 2** (có checkbox confirmation).

### 📁 Files đã tạo/cập nhật:

#### 1. Database
- ✅ `database_migration_merge_wallet.sql` - Migration script tạo bảng wallet_merge_history

#### 2. Entity
- ✅ `entity/WalletMergeHistory.java` - Entity lưu lịch sử merge

#### 3. Repository
- ✅ `repository/WalletMergeHistoryRepository.java` - Repository mới
- ✅ `repository/TransactionRepository.java` - Đã thêm methods cho merge

#### 4. DTOs
- ✅ `dto/MergeWalletRequest.java` - Request body
- ✅ `dto/MergeWalletResponse.java` - Response sau merge
- ✅ `dto/MergeWalletPreviewResponse.java` - Preview trước merge
- ✅ `dto/MergeCandidateDTO.java` - Thông tin ví có thể gộp

#### 5. Service
- ✅ `service/WalletService.java` - Đã thêm 3 methods interface
- ✅ `service/impl/WalletServiceImpl.java` - Đã implement đầy đủ logic

#### 6. Controller
- ✅ `controller/WalletController.java` - Đã thêm 4 endpoints

---

## 🗄️ BƯỚC 1: CHẠY DATABASE MIGRATION

```bash
# Kết nối MySQL
mysql -u root -p

# Chọn database
use finance_db;

# Chạy migration
source backend/database_migration_merge_wallet.sql
```

**Hoặc copy nội dung file SQL và chạy trong MySQL Workbench/phpMyAdmin**

---

## ▶️ BƯỚC 2: KHỞI ĐỘNG BACKEND

```bash
cd backend

# Build project
mvn clean install

# Hoặc chạy trực tiếp
mvn spring-boot:run
```

Server sẽ chạy tại: `http://localhost:8080`

---

## 🧪 BƯỚC 3: TEST API ENDPOINTS

### 3.1. Lấy danh sách ví có thể gộp

```http
GET http://localhost:8080/wallets/{sourceWalletId}/merge-candidates
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "candidateWallets": [
    {
      "walletId": 3,
      "walletName": "Techcombank",
      "currencyCode": "VND",
      "balance": 10000000,
      "transactionCount": 120,
      "isDefault": false,
      "canMerge": true,
      "reason": null
    }
  ],
  "ineligibleWallets": [
    {
      "walletId": 5,
      "walletName": "Ví USD",
      "currencyCode": "USD",
      "balance": 1000,
      "canMerge": false,
      "reason": "Khác loại tiền tệ (USD ≠ VND)"
    }
  ],
  "total": 1
}
```

---

### 3.2. Preview merge

```http
GET http://localhost:8080/wallets/{targetWalletId}/merge-preview?sourceWalletId={sourceId}
Authorization: Bearer YOUR_JWT_TOKEN
```

**Response:**
```json
{
  "preview": {
    "sourceWalletName": "Ví tiền mặt",
    "sourceBalance": 2500000,
    "sourceTransactionCount": 45,
    "targetWalletName": "Techcombank",
    "targetBalance": 10000000,
    "targetTransactionCount": 120,
    "finalBalance": 12500000,
    "totalTransactions": 165,
    "willTransferDefaultFlag": true,
    "canProceed": true,
    "warnings": [
      "Ví 'Ví tiền mặt' sẽ bị xóa vĩnh viễn",
      "45 giao dịch sẽ được chuyển sang ví đích",
      "Cờ 'Ví mặc định' sẽ chuyển sang ví đích",
      "Hành động này không thể hoàn tác"
    ]
  }
}
```

---

### 3.3. Thực hiện merge

```http
POST http://localhost:8080/wallets/{targetWalletId}/merge
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "sourceWalletId": 1
}
```

**Response Success:**
```json
{
  "success": true,
  "message": "Gộp ví thành công",
  "result": {
    "targetWalletId": 3,
    "targetWalletName": "Techcombank",
    "finalBalance": 12500000,
    "finalCurrency": "VND",
    "mergedTransactions": 45,
    "sourceWalletName": "Ví tiền mặt",
    "wasDefaultTransferred": true,
    "mergeHistoryId": 1,
    "mergedAt": "2025-01-11T14:30:00"
  }
}
```

**Response Error:**
```json
{
  "success": false,
  "error": "Chỉ có thể gộp các ví cùng loại tiền tệ"
}
```

---

## 🎯 BUSINESS RULES

### Điều kiện để gộp ví:
1. ✅ User phải là OWNER của cả 2 ví
2. ✅ Cả 2 ví phải CÙNG currency_code (VND + VND, USD + USD)
3. ✅ Cả 2 ví phải là ví cá nhân (không có shared members)
4. ✅ Ví nguồn ≠ Ví đích
5. ✅ Cả 2 ví phải tồn tại và active

### Khi merge thành công:
- ✅ Tất cả transactions từ ví nguồn → ví đích
- ✅ Balance = targetBalance + sourceBalance
- ✅ Nếu ví nguồn là default → ví đích trở thành default
- ✅ Ví nguồn bị XÓA VĨNH VIỄN
- ✅ Lịch sử merge được lưu vào wallet_merge_history

---

## 🔐 SECURITY

- ✅ Tất cả endpoints yêu cầu JWT authentication
- ✅ Kiểm tra ownership trước mọi thao tác
- ✅ Validation đầy đủ ở cả service và controller layer
- ✅ Transaction-based merge (ACID compliance)
- ✅ Audit trail (merge history)

---

## 📊 DATABASE SCHEMA

```sql
-- Bảng lưu lịch sử merge
CREATE TABLE wallet_merge_history (
    merge_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_wallet_id BIGINT NOT NULL,
    source_wallet_name VARCHAR(100) NOT NULL,
    source_balance DECIMAL(15,2) NOT NULL,
    target_wallet_id BIGINT NOT NULL,
    target_wallet_name VARCHAR(100) NOT NULL,
    target_balance_before DECIMAL(15,2) NOT NULL,
    target_balance_after DECIMAL(15,2) NOT NULL,
    merged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- ... other fields
);
```

---

## 🎨 FRONTEND INTEGRATION

### Flow theo OPTION 2:

```javascript
// 1. Lấy danh sách ví có thể gộp
const candidates = await fetch(`/api/wallets/${sourceWalletId}/merge-candidates`);

// 2. User chọn ví đích → Auto load preview
const preview = await fetch(`/api/wallets/${targetId}/merge-preview?sourceWalletId=${sourceId}`);

// 3. Hiển thị preview với checkbox
// [ ] Tôi đã hiểu và đồng ý
// [Xác nhận gộp] (disabled until checked)

// 4. User check box → enable button

// 5. User click "Xác nhận gộp"
const result = await fetch(`/api/wallets/${targetId}/merge`, {
  method: 'POST',
  body: JSON.stringify({ sourceWalletId: sourceId })
});

// 6. Success → Show toast, refresh wallet list
```

### UI Components cần implement:

1. ✅ Dropdown "Chọn ví đích"
2. ✅ Preview section (auto-load khi chọn ví)
3. ✅ Checkbox confirmation
4. ✅ Button "Xác nhận gộp" (conditional enable)
5. ✅ Loading states
6. ✅ Success/Error messages
7. ✅ Wallet list refresh

---

## ⚠️ IMPORTANT NOTES

### 1. Không thể undo
- Merge là permanent action
- Ví nguồn bị xóa vĩnh viễn
- Chỉ có audit trail trong wallet_merge_history

### 2. Validation 2 lần
- Preview: validate và tính toán
- Merge: validate lại trước khi execute
- Đảm bảo data không thay đổi giữa preview và merge

### 3. Transaction-based
- Toàn bộ merge operation trong 1 transaction
- Nếu có lỗi → ROLLBACK toàn bộ
- Đảm bảo data integrity

### 4. Performance
- Với ví có nhiều transactions (>1000): có thể mất vài giây
- Cân nhắc thêm loading indicator
- Merge duration được lưu trong merge_duration_ms

---

## 🐛 TROUBLESHOOTING

### Lỗi: "Không thể gộp ví đã được chia sẻ"
→ Kiểm tra memberCount của ví, phải = 1

### Lỗi: "Chỉ có thể gộp các ví cùng loại tiền tệ"
→ Kiểm tra currency_code của 2 ví

### Lỗi: "Ví nguồn không tồn tại"
→ Ví đã bị xóa hoặc user không có quyền truy cập

### Merge thành công nhưng frontend không update
→ Gọi lại API GET /wallets để refresh danh sách

---

## 📈 NEXT STEPS (Optional)

### Improvements có thể thêm:
1. Soft delete thay vì hard delete (cho phép undo)
2. Email notification sau khi merge
3. Batch merge (gộp nhiều ví cùng lúc)
4. Merge history UI (xem lại lịch sử)
5. Analytics dashboard
6. Export merge history to CSV

---

## ✅ CHECKLIST DEPLOYMENT

- [ ] Chạy database migration
- [ ] Test tất cả API endpoints
- [ ] Kiểm tra authentication
- [ ] Test validation rules
- [ ] Test với nhiều loại tiền tệ
- [ ] Test với ví có/không có transactions
- [ ] Test concurrent requests
- [ ] Backup database trước khi deploy production
- [ ] Document API cho frontend team
- [ ] Training cho support team

---

## 📞 SUPPORT

Nếu có vấn đề, kiểm tra:
1. Log trong console (Spring Boot)
2. Database query logs
3. Network tab trong browser
4. wallet_merge_history table

---

**🎉 CHÚC MỪNG! Tính năng gộp ví đã sẵn sàng sử dụng!**

