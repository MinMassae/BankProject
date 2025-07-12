package com.example.bankproject.mapper;

import com.example.bankproject.entity.OTP;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OTPMapper {

    // 1. OTP 조회 (번호만 조회)
    OTP findByMemberNo(@Param("memberNo") int memberNo);

    // 2. 삽입
    void insertOTP(@Param("memberNo") int memberNo, @Param("otpNumber") String otpNumber);

    // 3. 업데이트
    void updateOTP(@Param("memberNo") int memberNo, @Param("otpNumber") String otpNumber);

    // 4. 모든 사용자 OTP 조회 (배치용)
    List<OTP> findAll();

    // 5. 인증용 조회
    OTP findByMemberNoAndOtpNumber(@Param("memberNo") int memberNo, @Param("otpNumber") String otpNumber);
}
