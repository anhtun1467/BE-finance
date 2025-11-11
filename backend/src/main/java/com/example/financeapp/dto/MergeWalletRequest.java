package com.example.financeapp.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO cho request gộp ví
 */
public class MergeWalletRequest {

    @NotNull(message = "Ví nguồn không được để trống")
    private Long sourceWalletId;

    // Constructors
    public MergeWalletRequest() {}

    public MergeWalletRequest(Long sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    // Getters & Setters
    public Long getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(Long sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }
}

