package com.allanweber.jwttoken.data;

import java.util.List;

public record AccessTokenInfo(
        String subject,
        Long tenancyId,
        String tenancyName,
        List<String> scopes) {
}
