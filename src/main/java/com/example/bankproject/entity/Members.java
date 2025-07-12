package com.example.bankproject.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Members {
    private Integer no;
    private String id;
    private String password;
    private String name;
    private LocalDateTime createdAt;
}
