package com.example.bankproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AccountCreateRequest {
    private String purpose;
    private String job;
    private String pin;
}
