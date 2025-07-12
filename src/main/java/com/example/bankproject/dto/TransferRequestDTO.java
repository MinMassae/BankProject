package com.example.bankproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class TransferRequestDTO {

    private String fromAccountNumber;
    private String toAccountNumber;
    private int amount;
    private String pin;
}
