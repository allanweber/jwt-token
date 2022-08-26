package com.allanweber.jwttoken.service;

import com.allanweber.jwttoken.contract.JwtUserData;
import com.allanweber.jwttoken.data.TokenData;

public interface JwtTokenProvider {

    TokenData generateAccessToken(JwtUserData applicationUser);

    TokenData generateRefreshToken(JwtUserData applicationUser);

    TokenData generateTemporaryToken(String email);
}
