package com.example.bankproject.controller;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Profile("api")
@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateCrawlController {

    @GetMapping("/naver")
    public ResponseEntity<Map<String, String>> getExchangeRateFromNaver() {
        Map<String, String> result = new HashMap<>();

        try {
            // 오늘부터 최대 5일 전까지 역으로 조회
            for (int i = 0; i < 5; i++) {
                LocalDate targetDate = LocalDate.now().minusDays(i);
                String formatted = targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                String url = "https://finance.naver.com/marketindex/exchangeDailyQuote.naver?marketindexCd=FX_USDKRW";
                Document doc = Jsoup.connect(url).get();

                Elements usdElements = doc.select("table.tbl_exchange tbody tr td:nth-child(2)");
                Elements jpyElements = Jsoup.connect("https://finance.naver.com/marketindex/exchangeDailyQuote.naver?marketindexCd=FX_JPYKRW").get()
                        .select("table.tbl_exchange tbody tr td:nth-child(2)");

                if (!usdElements.isEmpty() && !jpyElements.isEmpty()) {
                    result.put("usd", usdElements.first().text());
                    result.put("jpy", jpyElements.first().text());
                    break;
                }
            }

            if (!result.containsKey("usd") || !result.containsKey("jpy")) {
                return ResponseEntity.status(503).body(Map.of("error", "환율 데이터를 가져오지 못했습니다."));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }
}
