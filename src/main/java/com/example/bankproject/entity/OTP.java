package com.example.bankproject.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OTP {

    private int id;                  // PK
    private String otpNumber;        // 6자리 OTP
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 갱신 시간
    private int memberNo;            // 회원 번호
}
