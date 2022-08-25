package com.allanweber.jwttoken.service;

import com.allanweber.jwttoken.contract.JwtUser;
import com.allanweber.jwttoken.data.TokenData;

public interface JwtTokenProvider {

    TokenData generateAccessToken(JwtUser applicationUser);

    TokenData generateRefreshToken(JwtUser applicationUser);

    TokenData generateTemporaryToken(String email);
}
