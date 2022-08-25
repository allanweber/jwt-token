package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.JwtException;
import com.allanweber.jwttoken.data.JwtProperties;
import com.allanweber.jwttoken.service.JwtTokenParser;
import com.allanweber.jwttoken.service.PublicKeyProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtTokenParserImpl implements JwtTokenParser {

    private final Key publicKey;

    @SneakyThrows
    public JwtTokenParserImpl(PublicKeyProvider publicKeyProvider, JwtProperties properties) {
        this.publicKey = publicKeyProvider.provide(properties.getPublicKey());
    }

    @SuppressWarnings("PMD")
    @Override
    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.publicKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException exception) {
            throw new JwtException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse expired JWT : %s failed : %s", token, exception.getMessage()));
        } catch (UnsupportedJwtException exception) {
            throw new JwtException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse unsupported JWT : %s failed : %s", token, exception.getMessage()));
        } catch (MalformedJwtException exception) {
            throw new JwtException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse invalid JWT : %s failed : %s", token, exception.getMessage()));
        } catch (SignatureException exception) {
            throw new JwtException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse JWT with invalid signature : %s failed : %s", token,
                            exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            throw new JwtException(HttpStatus.UNAUTHORIZED,
                    String.format("Request to parse empty or null JWT : %s failed : %s", token,
                            exception.getMessage()));
        }
    }
}
