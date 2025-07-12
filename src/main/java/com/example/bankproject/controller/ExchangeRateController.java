package com.example.bankproject.controller;

import com.example.bankproject.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Profile("api")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final String AUTH_KEY = "mRG6TJPotrvxqGvxkisfZ5yHKH5ZFmZU";
    /*
    @GetMapping("/exchange-rate/history")
    public ResponseEntity<Map<String, List<ExchangeRateResponse>>> getExchangeRateHistory() {
        Map<String, List<ExchangeRateResponse>> result = new LinkedHashMap<>();
        RestTemplate restTemplate = new RestTemplate();

        System.out.println("=== 환율 수집 시작 ===");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(i);
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String url = String.format(
                    "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=%s&searchdate=%s&data=AP01",
                    AUTH_KEY, formattedDate);

            try {
                ResponseEntity<ExchangeRateResponse[]> response =
                        restTemplate.getForEntity(url, ExchangeRateResponse[].class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<ExchangeRateResponse> filtered = Arrays.stream(response.getBody())
                            .filter(item -> item.getCur_unit().contains("USD") || item.getCur_unit().contains("JPY"))
                            .toList();

                    if (!filtered.isEmpty()) {
                        result.put(formattedDate, filtered);
                    }
                }
            } catch (Exception e) {
                System.err.println("환율 조회 실패 (" + formattedDate + "): " + e.getMessage());
            }
        }

        System.out.println("=== 환율 수집 완료 ===");

        return ResponseEntity.ok(result); // 비어 있어도 빈 JSON 반환
    }
     */
}
