package com.example.bankproject.controller;

import com.example.bankproject.JwtProvider;
import com.example.bankproject.dto.AccountDTO;
import com.example.bankproject.dto.LoginRequest;
import com.example.bankproject.dto.MeResponseDTO;
import com.example.bankproject.entity.Account;
import com.example.bankproject.entity.Members;
import com.example.bankproject.service.AccountService;
import com.example.bankproject.service.MembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("api")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class AuthController {

    private final MembersService membersService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider JwtProvider;
    private final AccountService accountService; // ✅ 추가

    // ✅ 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Members members) {
        membersService.register(members);
        return ResponseEntity.ok("회원가입 성공");
    }

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginUser) {
        Members members = membersService.findById(loginUser.getId());

        if (members == null || !passwordEncoder.matches(loginUser.getPassword(), members.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = JwtProvider.generateToken(members.getId());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // ✅ 로그인된 사용자 정보 + 계좌 정보 반환
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 없음");
        }

        String token = authHeader.substring(7);
        if (!JwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 무효");
        }

        String userId = JwtProvider.getUserId(token);
        Members members = membersService.findById(userId);

        // 계좌 조회
        List<Account> accounts = accountService.getAccountsByMemberNo(members.getNo());

        List<AccountDTO> accountDTOs = accounts.stream()
                .map(account -> AccountDTO.builder()
                        .no(account.getNo())
                        .accountNumber(account.getAccountNumber())
                        .balance(account.getBalance())
                        .job(account.getJob())
                        .createdAt(account.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        MeResponseDTO response = MeResponseDTO.builder()
                .name(members.getName())
                .accounts(accountDTOs)
                .memberNo(members.getNo())
                .build();

        return ResponseEntity.ok(response);
    }

    // ✅ 아이디 중복 확인
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("id") String id) {
        boolean available = membersService.isIdAvailable(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return ResponseEntity.ok(response);
    }
}
