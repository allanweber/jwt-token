package com.allanweber.jwttoken.service;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtResolveToken {

    String resolve(HttpServletRequest request);

}
