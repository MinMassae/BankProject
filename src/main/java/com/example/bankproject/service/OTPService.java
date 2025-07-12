package com.example.bankproject.service;

import com.example.bankproject.entity.OTP;
import com.example.bankproject.mapper.OTPMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPService {

    private final OTPMapper otpMapper;

    // 🔸 1. OTP 생성 또는 갱신
    public void createOrUpdateOTP(int memberNo) {
        String otpNumber = generateOTP();
        OTP existingOTP = otpMapper.findByMemberNo(memberNo);

        if (existingOTP == null) {
            otpMapper.insertOTP(memberNo, otpNumber);
        } else {
            otpMapper.updateOTP(memberNo, otpNumber);
        }
    }

    // 🔸 2. 특정 멤버의 OTP 반환
    public OTP getOTPByMemberNo(int memberNo) {
        return otpMapper.findByMemberNo(memberNo);
    }

    // 🔸 3. OTP 인증 처리
    public boolean verifyOTP(int memberNo, String otpNumber) {
        OTP otp = otpMapper.findByMemberNoAndOtpNumber(memberNo, otpNumber);
        return otp != null;
    }

    // 🔸 4. 배치에서 모든 OTP 갱신
    public void updateAllOTPs() {
        List<OTP> otpList = otpMapper.findAll(); // findAll() 메서드 Mapper에 구현 필요
        for (OTP otp : otpList) {
            String newOtp = generateOTP();
            otpMapper.updateOTP(otp.getMemberNo(), newOtp);
        }
    }

    // 🔸 5. 6자리 OTP 생성기
    public String generateOTP() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
