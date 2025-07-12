package com.example.bankproject.controller;

import com.example.bankproject.dto.AccountHistoryResponseDTO;
import com.example.bankproject.dto.TransferHistoryResponseDTO;
import com.example.bankproject.dto.TransferRequestDTO;
import com.example.bankproject.entity.Account;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountPageController {

    private final AccountService accountService;
    private final JwtProvider jwtProvider;

    // 🔸 잔액 조회
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.status(404).body("口座が存在しません。");
        }
        return ResponseEntity.ok(Map.of("balance", account.getBalance()));
    }

    // 🔸 거래 내역 조회 (DTO 반환)
    @GetMapping("/{accountNumber}/history")
    public ResponseEntity<?> getTransactionHistory(@PathVariable String accountNumber) {
        List<AccountHistoryResponseDTO> histories = accountService.findTransactionHistory(accountNumber);
        return ResponseEntity.ok(histories);
    }

    // 🔸 이체
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequestDTO dto, HttpServletRequest request) {
        String userId = extractUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("ログイン情報が確認できません。");
        }

        boolean result = accountService.transfer(dto);
        if (result) {
            return ResponseEntity.ok("振込成功！");
        } else {
            return ResponseEntity.badRequest().body("口座番号またはPINが一致しません。");
        }
    }

    // 🔸 이체 내역 조회 API
    @GetMapping("/{accountNumber}/transfer-history")
    public ResponseEntity<?> getTransferHistory(@PathVariable String accountNumber) {
        List<TransferHistoryResponseDTO> histories = accountService.findTransferHistory(accountNumber);
        return ResponseEntity.ok(histories);
    }

    // ✅ 토큰에서 userId 추출
    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtProvider.getUserId(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}