package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.service.JwtResolveToken;
import com.allanweber.jwttoken.service.JwtTokenAuthenticationCheck;
import com.allanweber.jwttoken.service.JwtTokenReader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class JwtTokenAuthenticationCheckImpl implements JwtTokenAuthenticationCheck {

    private final JwtTokenReader tokenReader;
    private final JwtResolveToken resolveToken;

    public JwtTokenAuthenticationCheckImpl(JwtTokenReader tokenReader) {
        super();
        this.tokenReader = tokenReader;
        this.resolveToken = new JwtResolveTokenHttpHeader();
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        String token = resolveToken.resolve(request);
        if (StringUtils.hasText(token) && this.tokenReader.isTokenValid(token)) {
            usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) this.tokenReader.getAuthentication(token);
        }
        return usernamePasswordAuthenticationToken;
    }
}
