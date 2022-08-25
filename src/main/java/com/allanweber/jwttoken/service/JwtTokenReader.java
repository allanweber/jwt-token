package com.allanweber.jwttoken.service;

import com.allanweber.jwttoken.data.AccessTokenInfo;
import org.springframework.security.core.Authentication;

public interface JwtTokenReader {
    Authentication getAuthentication(String token);

    AccessTokenInfo getAccessTokenInfo(String token);

    AccessTokenInfo getTokenInfo(String token);

    boolean isTokenValid(String token);
}
