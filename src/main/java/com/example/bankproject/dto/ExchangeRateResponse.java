package com.example.bankproject.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ExchangeRateResponse {
    private String cur_unit;     // 통화 코드 (e.g. USD, JPY)
    private String deal_bas_r;   // 매매 기준율
}