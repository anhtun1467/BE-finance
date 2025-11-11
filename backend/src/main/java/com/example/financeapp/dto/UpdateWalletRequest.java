package com.example.financeapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletRequest {

    @Size(max = 100, message = "Tên ví không được vượt quá 100 ký tự")
    private String walletName;

    private String description;

    private String currencyCode; // Mã tiền tệ (VD: VND, USD,...)

    @DecimalMin(value = "0.0", inclusive = true, message = "Số dư không hợp lệ")
    private BigDecimal balance; // Chỉ cho sửa nếu ví chưa có giao dịch
}
