package com.example.financeapp.auth.service;

import com.example.financeapp.auth.dto.LoginRequest;
import com.example.financeapp.auth.dto.RegisterRequest;

public interface AuthService {
    String login(LoginRequest request);
    String register(RegisterRequest request);
}
