package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.helper.JwtConstants;
import com.allanweber.jwttoken.service.JwtResolveToken;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@NoArgsConstructor
public class JwtResolveTokenHttpHeader implements JwtResolveToken {

    @Override
    public String resolve(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = bearerToken.replace(JwtConstants.TOKEN_PREFIX, "");
        }
        return token;
    }

}
