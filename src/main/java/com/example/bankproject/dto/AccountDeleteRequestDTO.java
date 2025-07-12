package com.example.bankproject.dto;

import lombok.Data;

@Data
public class AccountDeleteRequestDTO {
    private String accountNumber;
    private String pin;
}
