package com.example.bankproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Data
@AllArgsConstructor
public class TransferHistoryResponseDTO {

    private String fromAccountNumber; // 송금한 계좌 번호
    private String toAccountNumber;   // 송금받은 계좌 번호
    private double amount;            // 금액
    private String transactionTime;   // 이체 시간
}