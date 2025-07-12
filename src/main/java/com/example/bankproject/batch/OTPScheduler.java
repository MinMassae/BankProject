package com.example.bankproject.batch;

import com.example.bankproject.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class OTPScheduler {

    private final OTPService otpService;

    @Scheduled(fixedRate = 10000)
    public void updateOTPBatch() {
        otpService.updateAllOTPs();
    }
}
