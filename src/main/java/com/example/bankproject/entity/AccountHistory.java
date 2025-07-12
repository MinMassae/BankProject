package com.example.bankproject.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter


public class AccountHistory {
    private int no;               // 거래 PK
    private int accountNo;        // 계좌 PK
    private String type;          // 入金 / 出金
    private double amount;        // 거래 금액
    private double balanceAfter;  // 거래 후 잔액
    private LocalDateTime createdAt; // 거래 시간
    private String memo;
}
