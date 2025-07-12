package com.example.bankproject.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AccountHistoryResponseDTO {

    private String type;           // 入金 / 出金
    private double amount;         // 거래 금액
    private double balanceAfter;   // 거래 후 잔액
    private String transactionTime; // 거래 시간 (문자열로 반환)
    private String memo;            // 메모 (선택 사항, 필요 시 추가)

    // 생성자
    public AccountHistoryResponseDTO(String type, double amount, double balanceAfter, String transactionTime, String memo) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transactionTime = transactionTime;
        this.memo = memo;
    }
}
