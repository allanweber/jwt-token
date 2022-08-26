package com.allanweber.jwttoken.impl;

import com.allanweber.jwttoken.JwtException;
import com.allanweber.jwttoken.JwtUserTest;
import com.allanweber.jwttoken.data.JwtProperties;
import com.allanweber.jwttoken.data.ServiceType;
import com.allanweber.jwttoken.data.TokenData;
import com.allanweber.jwttoken.service.JwtTokenProvider;
import com.allanweber.jwttoken.service.PrivateKeyProvider;
import com.allanweber.jwttoken.service.impl.JwtTokenProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JwtTokenProviderImplTest {

    @Mock
    PrivateKeyProvider privateKeyProvider;

    JwtTokenProvider tokenProvider;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException, IOException {
        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 3600L,
                86400L, "issuer", "audience");
        when(privateKeyProvider.provide(new File("arg"))).thenReturn(mockPrivateKey());
        tokenProvider = new JwtTokenProviderImpl(properties, privateKeyProvider);
    }

    @Test
    @DisplayName("Given user and its roles when generateAccessToken, return JWT token")
    void generateAccessToken() {
        JwtUserTest applicationUser = JwtUserTest.builder()
                .userEmail("mail@mail.com")
                .userAuthoritiesName(Collections.singletonList("ROLE_USER"))
                .userTenancyId(1L)
                .userTenancyName("tenancy")
                .build();
        TokenData tokenData = tokenProvider.generateAccessToken(applicationUser);

        assertThat(tokenData.token()).isNotEmpty();
        assertThat(tokenData.issuedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Given user and its roles when generateRefreshToken, return JWT token")
    void generateRefreshToken() {
        JwtUserTest applicationUser = JwtUserTest.builder()
                .userEmail("mail@mail.com")
                .userAuthoritiesName(Collections.singletonList("ROLE_USER"))
                .userTenancyId(1L)
                .userTenancyName("tenancy")
                .build();

        TokenData tokenData = tokenProvider.generateRefreshToken(applicationUser);

        assertThat(tokenData.token()).isNotEmpty();
        assertThat(tokenData.issuedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Given email generate a temporary token, return JWT token")
    void generateTemporaryToken() {
        String email = "mail@mail.com";
        TokenData tokenData = tokenProvider.generateTemporaryToken(email);
        assertThat(tokenData.token()).isNotEmpty();
        assertThat(tokenData.issuedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Given user with null roles when generateAccessToken, return exception")
    void nullRoles() {
        JwtUserTest applicationUser = JwtUserTest.builder()
                .userEmail("mail@mail.com")
                .userAuthoritiesName(null)
                .userTenancyId(1L)
                .userTenancyName("tenancy")
                .build();

        JwtException exception = assertThrows(
                JwtException.class,
                () -> tokenProvider.generateAccessToken(applicationUser),
                "User has no authorities");

        assertThat(exception.getRawStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("Given user with empty roles when generateAccessToken, return exception")
    void emptyRoles() throws IOException, NoSuchAlgorithmException {
        JwtUserTest applicationUser = JwtUserTest.builder()
                .userEmail("mail@mail.com")
                .userAuthoritiesName(emptyList())
                .userTenancyId(1L)
                .userTenancyName("tenancy")
                .build();

        when(privateKeyProvider.provide(new File("arg"))).thenReturn(mockPrivateKey());

        JwtException exception = assertThrows(
                JwtException.class,
                () -> tokenProvider.generateAccessToken(applicationUser),
                "User has no authorities");

        assertThat(exception.getRawStatusCode()).isEqualTo(401);
    }

    private PrivateKey mockPrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.genKeyPair();
        return keyPair.getPrivate();
    }
}
