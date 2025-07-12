package com.example.bankproject.controller;

import com.example.bankproject.JwtProvider;
import com.example.bankproject.dto.AccountCreateRequest;
import com.example.bankproject.dto.AccountDeleteRequestDTO;
import com.example.bankproject.entity.Members;
import com.example.bankproject.entity.Account;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.MembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Profile("api")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class AccountController {

    private final AccountService accountService;
    private final MembersService membersService;
    private final JwtProvider jwtProvider;

    // ✅ 로그인한 사용자의 계좌 목록 조회
    @GetMapping("/accounts")
    public ResponseEntity<?> getMyAccounts(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("トークンが必要です。");
        }

        String token = authHeader.substring(7);
        if (!jwtProvider.validateToken(token)) {
            return ResponseEntity.status(401).body("トークンが無効です。");
        }

        String userId = jwtProvider.getUserId(token);
        Members member = membersService.findById(userId);

        if (member == null) {
            return ResponseEntity.status(404).body("ユーザーが存在しません。");
        }

        List<Account> accounts = accountService.getAccountsByMemberNo(member.getNo());
        return ResponseEntity.ok(accounts);
    }

    // ✅ 계좌 개설 API
    @PostMapping("/account/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreateRequest dto, HttpServletRequest request) {
        System.out.println("🟡 계좌 개설 API 도착");

        String userId = extractUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("ログイン情報が確認できません。");
        }

        Members member = membersService.findById(userId);
        if (member == null) {
            return ResponseEntity.status(404).body("ユーザーが存在しません。");
        }

        if (!accountService.canOpenAccount(member.getNo())) {
            return ResponseEntity.badRequest().body("口座は2つまで作成できます。");
        }

        if (dto.getPin() == null || !String.valueOf(dto.getPin()).matches("\\d{4}")) {
            return ResponseEntity.badRequest().body("4桁の暗証番号を入力してください。");
        }

        accountService.openAccount(dto, member.getNo());

        return ResponseEntity.ok("口座が正常に作成されました。");
    }

    // ✅ 계좌 삭제 API
    @PostMapping("/account/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody AccountDeleteRequestDTO dto,
                                           HttpServletRequest request) {
        String userId = extractUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body("ログイン情報が確認できません。");
        }

        Members member = membersService.findById(userId);
        if (member == null) {
            return ResponseEntity.status(404).body("ユーザーが存在しません。");
        }

        // 🔍 PIN 4자리 체크
        if (dto.getPin() == null || !dto.getPin().matches("\\d{4}")) {
            return ResponseEntity.badRequest().body("4桁の暗証番号を入力してください。");
        }

        boolean success = accountService.deleteAccount(dto.getAccountNumber(), dto.getPin(), member.getNo());

        if (success) {
            return ResponseEntity.ok("口座が削除されました。");
        } else {
            return ResponseEntity.status(400).body("口座番号またはPINが一致しません。");
        }
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
