//package com.allanweber.jwttoken.impl;
//
//import com.allanweber.jwttoken.JwtUserTest;
//import com.allanweber.jwttoken.data.JwtProperties;
//import com.allanweber.jwttoken.data.TokenData;
//import com.allanweber.jwttoken.helper.JwtConstants;
//import com.allanweber.jwttoken.service.JwtTokenParser;
//import com.allanweber.jwttoken.service.JwtTokenProvider;
//import com.allanweber.jwttoken.service.impl.JwtTokenParserImpl;
//import com.allanweber.jwttoken.service.impl.JwtTokenProviderImpl;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(classes = {JwtTokenProviderImpl.class, JwtTokenParserImpl.class, JwtProperties.class})
//@PropertySource(value = "application.properties")
//public class JwtIntegratedTest {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Autowired
//    private JwtTokenParser jwtTokenParser;
//
//    @DisplayName("Generate and read access token")
//    @Test
//    void generateAndReadAccessToken() throws JsonProcessingException {
//        JwtUserTest applicationUser = JwtUserTest.builder()
//                .email("mail@mail.com")
//                .authorities(Collections.singletonList("ROLE_USER"))
//                .tenancyId(1L)
//                .tenancyName("tenancy")
//                .build();
//
//        TokenData accessToken = jwtTokenProvider.generateAccessToken(applicationUser);
//
//        assertThat(accessToken.token()).isNotBlank();
//        Jws<Claims> accessTokenClaims = jwtTokenParser.parseToken(accessToken.token());
//        assertThat(accessTokenClaims.getBody().getSubject()).isEqualTo(applicationUser.getEmail());
//
//        assertThat(accessTokenClaims.getBody().get(JwtConstants.TENANCY_NAME).toString()).isEqualTo(applicationUser.getTenancyName());
//        assertThat(accessTokenClaims.getBody().getAudience()).isEqualTo("trade-journal");
//        assertThat(accessTokenClaims.getBody().getIssuer()).isEqualTo("https://tradejournal.biz");
//        Date start = Date.from(LocalDateTime.now().plusSeconds(3500L).atZone(ZoneId.systemDefault()).toInstant());
//        Date end = Date.from(LocalDateTime.now().plusSeconds(3600L).atZone(ZoneId.systemDefault()).toInstant());
//        assertThat(accessTokenClaims.getBody().getExpiration()).isBetween(start, end);
//        List<String> scopes = ((List<?>) accessTokenClaims.getBody().get(JwtConstants.SCOPES))
//                .stream()
//                .map(authority -> new SimpleGrantedAuthority((String) authority))
//                .map(SimpleGrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        assertThat(scopes).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
//    }
//
//    @DisplayName("Generate and read refresh token")
//    @Test
//    void generateAndReadRefreshToken() {
//        JwtUserTest applicationUser = JwtUserTest.builder()
//                .email("mail@mail.com")
//                .authorities(Collections.singletonList("ROLE_USER"))
//                .tenancyId(1L)
//                .tenancyName("tenancy")
//                .build();
//
//        TokenData refreshToken = jwtTokenProvider.generateRefreshToken(applicationUser);
//        assertThat(refreshToken.token()).isNotBlank();
//        Jws<Claims> refreshTokenClaims = jwtTokenParser.parseToken(refreshToken.token());
//        assertThat(refreshTokenClaims.getBody().getSubject()).isEqualTo(applicationUser.getEmail());
//        assertThat(refreshTokenClaims.getBody().get(JwtConstants.TENANCY_ID)).isNull();
//        assertThat(refreshTokenClaims.getBody().getAudience()).isEqualTo("trade-journal");
//        assertThat(refreshTokenClaims.getBody().getIssuer()).isEqualTo("https://tradejournal.biz");
//        assertThat(refreshTokenClaims.getBody().getIssuer()).isEqualTo("https://tradejournal.biz");
//        Date start = Date.from(LocalDateTime.now().plusSeconds(86300L).atZone(ZoneId.systemDefault()).toInstant());
//        Date end = Date.from(LocalDateTime.now().plusSeconds(86400L).atZone(ZoneId.systemDefault()).toInstant());
//        assertThat(refreshTokenClaims.getBody().getExpiration()).isBetween(start, end);
//        List<String> scopes = ((List<?>) refreshTokenClaims.getBody().get(JwtConstants.SCOPES))
//                .stream()
//                .map(authority -> new SimpleGrantedAuthority((String) authority))
//                .map(SimpleGrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        assertThat(scopes).containsExactly("REFRESH_TOKEN");
//    }
//
//    @DisplayName("Generate and read temporary token")
//    @Test
//    void generateAndReadTemporaryToken() {
//        TokenData refreshToken = jwtTokenProvider.generateTemporaryToken("mail@mail.com");
//        assertThat(refreshToken.token()).isNotBlank();
//        Jws<Claims> refreshTokenClaims = jwtTokenParser.parseToken(refreshToken.token());
//        assertThat(refreshTokenClaims.getBody().getSubject()).isEqualTo("mail@mail.com");
//        assertThat(refreshTokenClaims.getBody().get(JwtConstants.TENANCY_ID)).isNull();
//        assertThat(refreshTokenClaims.getBody().getAudience()).isEqualTo("trade-journal");
//        assertThat(refreshTokenClaims.getBody().getIssuer()).isEqualTo("https://tradejournal.biz");
//        assertThat(refreshTokenClaims.getBody().getIssuer()).isEqualTo("https://tradejournal.biz");
//        Date start = Date.from(LocalDateTime.now().plusSeconds(604790).atZone(ZoneId.systemDefault()).toInstant());
//        Date end = Date.from(LocalDateTime.now().plusSeconds(604810).atZone(ZoneId.systemDefault()).toInstant());
//        assertThat(refreshTokenClaims.getBody().getExpiration()).isBetween(start, end);
//        List<String> scopes = ((List<?>) refreshTokenClaims.getBody().get(JwtConstants.SCOPES))
//                .stream()
//                .map(authority -> new SimpleGrantedAuthority((String) authority))
//                .map(SimpleGrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//        assertThat(scopes).containsExactly("TEMPORARY_TOKEN");
//    }
//}
