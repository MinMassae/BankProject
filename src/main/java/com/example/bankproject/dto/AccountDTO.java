package com.example.bankproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Integer no;                    // 계좌 고유 번호
    private String accountNumber;         // 계좌번호
    private Double balance;               // 잔액
    private String job;                   // 직업
    private LocalDateTime createdAt;      // 개설일
}
