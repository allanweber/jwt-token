package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.helper.JwtConstants;
import com.allanweber.jwttoken.service.JwtTokenReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class JwtTokenAuthenticationCheckImplTest {

    @Mock
    JwtTokenReader tokenReader;

    JwtTokenAuthenticationCheckImpl jwtTokenAuthenticationCheck;

    @BeforeEach
    public void setUp() {
        jwtTokenAuthenticationCheck = new JwtTokenAuthenticationCheckImpl(tokenReader);
    }

    @Test
    @DisplayName("Given server request with token process request successfully")
    void serverRequestSuccess() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(JwtConstants.TOKEN_PREFIX.concat("123456789"));

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer 123456789");
        when(tokenReader.isTokenValid("123456789")).thenReturn(true);
        when(tokenReader.getAuthentication("123456789")).thenReturn(new UsernamePasswordAuthenticationToken("user", null, emptyList()));

        UsernamePasswordAuthenticationToken authentication = jwtTokenAuthenticationCheck.getAuthentication(request);

        assertThat(authentication.getPrincipal()).isEqualTo("user");
    }

    @Test
    @DisplayName("Given server request without token process request successfully")
    void serverRequestSuccessWithoutToken() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        UsernamePasswordAuthenticationToken authentication = jwtTokenAuthenticationCheck.getAuthentication(request);

        assertThat(authentication).isNull();

        verify(tokenReader, never()).isTokenValid(anyString());
        verify(tokenReader, never()).getAuthentication(anyString());
    }
}