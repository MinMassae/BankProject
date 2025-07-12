package com.example.bankproject.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeResponseDTO {
    private String name;
    private List<AccountDTO> accounts;
    private int memberNo;
}
