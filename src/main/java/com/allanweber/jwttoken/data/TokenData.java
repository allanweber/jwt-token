package com.allanweber.jwttoken.data;

import java.time.LocalDateTime;

public record TokenData(
        String token,
        LocalDateTime issuedAt) {
}