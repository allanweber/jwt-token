package com.allanweber.jwttoken.impl;

import com.allanweber.jwttoken.data.AccessTokenInfo;
import com.allanweber.jwttoken.helper.JwtConstants;
import com.allanweber.jwttoken.service.JwtTokenReader;
import com.allanweber.jwttoken.service.impl.AccessTokenResolver;
import com.allanweber.jwttoken.service.impl.JwtResolveTokenHttpHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AccessTokenResolverTest {

    @Mock
    MethodParameter methodParameter;

    @Mock
    ModelAndViewContainer modelAndViewContainer;

    @Mock
    WebDataBinderFactory webDataBinderFactory;

    @Mock
    JwtTokenReader tokenReader;

    AccessTokenResolver accessTokenResolver;

    @BeforeEach
    public void setUp() {
        accessTokenResolver = new AccessTokenResolver(tokenReader, new JwtResolveTokenHttpHeader());
    }

    @DisplayName("Given a access token resolve into AccessTokenInfo")
    @Test
    public void retrieveTokenInfo() {
        String token = UUID.randomUUID().toString();

        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(nativeWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(request);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(JwtConstants.TOKEN_PREFIX.concat(token));

        when(tokenReader.getAccessTokenInfo(token)).thenReturn(
                new AccessTokenInfo(
                        "UserAdm",
                        1L,
                        "tenancy_1",
                        singletonList("USER")));

        AccessTokenInfo tokenInfo = (AccessTokenInfo) accessTokenResolver
                .resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);

        assert tokenInfo != null;
        assertThat(tokenInfo.subject()).isEqualTo("UserAdm");
        assertThat(tokenInfo.tenancyName()).isEqualTo("tenancy_1");
        assertThat(tokenInfo.scopes()).containsExactly("USER");
    }
}
