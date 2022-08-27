package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.JwtException;
import com.allanweber.jwttoken.contract.JwtUserData;
import com.allanweber.jwttoken.data.JwtProperties;
import com.allanweber.jwttoken.data.TokenData;
import com.allanweber.jwttoken.helper.DateHelper;
import com.allanweber.jwttoken.helper.JwtConstants;
import com.allanweber.jwttoken.service.JwtTokenProvider;
import com.allanweber.jwttoken.service.PrivateKeyProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@ConditionalOnProperty(prefix = "application.authentication.jwt", name = "service-type", havingValue = "PROVIDER")
@Slf4j
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final JwtProperties properties;
    private final Key privateKey;

    public JwtTokenProviderImpl(JwtProperties properties, PrivateKeyProvider privateKeyProvider) {
        this.properties = properties;
        try {
            this.privateKey = privateKeyProvider.provide(this.properties.getPrivateKey());
        } catch (IOException e) {
            log.error("Failed to load RSA private key", e);
            throw (JwtException) new JwtException(HttpStatus.UNAUTHORIZED,
                    "Failed to load RSA private key").initCause(e);
        }
    }

    @Override
    public TokenData generateAccessToken(JwtUserData user) {
        List<String> authorities = new ArrayList<>(ofNullable(user.getUserAuthoritiesName())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new JwtException(HttpStatus.UNAUTHORIZED, "User has no authorities")));

        Date issuedAt = DateHelper.getUTCDatetimeAsDate();
        String accessToken = Jwts.builder()
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer(properties.getIssuer())
                .setAudience(properties.getAudience())
                .setSubject(user.getUserEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(getExpirationDate(properties.getAccessTokenExpiration()))
                .claim(JwtConstants.SCOPES, authorities)
                .claim(JwtConstants.TENANCY_ID, user.getUserTenancyId())
                .claim(JwtConstants.TENANCY_NAME, user.getUserTenancyName())
                .compact();

        return new TokenData(accessToken,
                issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Override
    public TokenData generateRefreshToken(JwtUserData user) {
        Date issuedAt = DateHelper.getUTCDatetimeAsDate();
        String refreshToken = Jwts.builder()
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer(properties.getIssuer())
                .setAudience(properties.getAudience())
                .setSubject(user.getUserEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(getExpirationDate(properties.getRefreshTokenExpiration()))
                .claim(JwtConstants.SCOPES, Collections.singletonList(JwtConstants.REFRESH_TOKEN))
                .compact();

        return new TokenData(refreshToken,
                issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Override
    public TokenData generateTemporaryToken(String email) {
        Date issuedAt = DateHelper.getUTCDatetimeAsDate();
        String refreshToken = Jwts.builder()
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer(properties.getIssuer())
                .setAudience(properties.getAudience())
                .setSubject(email)
                .setIssuedAt(issuedAt)
                .setExpiration(getExpirationDate(604_800L))//expires in one week
                .claim(JwtConstants.SCOPES, Collections.singletonList(JwtConstants.TEMPORARY_TOKEN))
                .compact();

        return new TokenData(refreshToken,
                issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private Date getExpirationDate(Long expireIn) {
        return Date.from(LocalDateTime.now().plusSeconds(expireIn)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
