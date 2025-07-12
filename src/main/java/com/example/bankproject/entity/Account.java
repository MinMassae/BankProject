package com.example.bankproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private Integer no; // 계좌 PK

    private Integer membersNo; // members 테이블의 외래키

    private String accountNumber; // 계좌번호

    private Double balance; // 잔액

    private String job; // 직업

    private LocalDateTime createdAt; // 생성일시

    private String pin;
}