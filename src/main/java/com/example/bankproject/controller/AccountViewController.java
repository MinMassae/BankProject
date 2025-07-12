package com.example.bankproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Profile("view")
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountViewController {

    // ✅ Step 1: 약관 동의 페이지
    @GetMapping("/confirm")
    public String showConfirmationPage() {
        return "account_terms";
    }

    // ✅ Step 2: 계좌 개설 폼 페이지
    @GetMapping("/open-form")
    public String showOpenForm() {
        return "account_open_form";
    }

    // ✅ Step 3: 계좌 상세 페이지 이동
    @GetMapping("/{accountNumber}")
    public String showAccountDetailPage(@PathVariable String accountNumber) {
        return "account_page"; // account_page.html로 이동
    }
}
