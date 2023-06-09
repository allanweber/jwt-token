package com.allanweber.jwttoken.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtTokenAuthenticationCheck {

    UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request);
}
