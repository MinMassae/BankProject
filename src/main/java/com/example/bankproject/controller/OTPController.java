package com.example.bankproject.controller;

import com.example.bankproject.entity.OTP;
import com.example.bankproject.service.OTPService;
import com.example.bankproject.util.OTPStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OTPController {

    private final OTPService otpService;

    // 🔸 1. OTP 생성 API (버튼 클릭 시)
    @PostMapping("/generate")
    public ResponseEntity<?> generateOTP(@RequestParam int memberNo) {
        otpService.createOrUpdateOTP(memberNo);
        return ResponseEntity.ok("OTP 생성 완료");
    }

    // 🔸 2. OTP 조회 API (주기적 표시용)
    @GetMapping("/{memberNo}")
    public ResponseEntity<?> getOTP(@PathVariable int memberNo) {
        OTP otp = otpService.getOTPByMemberNo(memberNo);
        if (otp == null) {
            return ResponseEntity.badRequest().body("OTP 없음");
        }
        return ResponseEntity.ok(otp);
    }

    // 🔸 3. OTP 인증 API (모달에서 입력 후 호출)
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestParam int memberNo, @RequestParam String otpNumber) {
        boolean isValid = otpService.verifyOTP(memberNo, otpNumber);
        if (isValid) {
            OTPStore.verify(memberNo); // 인증 상태 저장
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }

    // 🔸 4. 인증 상태 확인 API (선택적 사용 가능)
    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam int memberNo) {
        boolean isVerified = OTPStore.isVerified(memberNo);
        return ResponseEntity.ok(isVerified);
    }

    // 🔸 5. 로그아웃 또는 인증 만료 시 인증 제거
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam int memberNo) {
        OTPStore.invalidate(memberNo);
        return ResponseEntity.ok("OTP 인증 해제됨");
    }
}
