package com.example.financeapp.auth.service.impl;

import com.example.financeapp.auth.dto.LoginRequest;
import com.example.financeapp.auth.dto.RegisterRequest;
import com.example.financeapp.auth.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(LoginRequest request) {
        // TODO: check email/password
        return "login from service";
    }

    @Override
    public String register(RegisterRequest request) {
        // TODO: create new user
        return "register from service";
    }
}
