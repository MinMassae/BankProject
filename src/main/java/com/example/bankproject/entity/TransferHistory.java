package com.example.bankproject.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferHistory {
    private int no;                // 이체 PK
    private int fromAccountNo;     // 보내는 계좌 PK
    private int toAccountNo;       // 받는 계좌 PK
    private double amount;         // 이체 금액
    private LocalDateTime createdAt; // 이체 시간
}
