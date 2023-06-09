package com.allanweber.jwttoken.impl;

import com.allanweber.jwttoken.JwtException;
import com.allanweber.jwttoken.data.JwtProperties;
import com.allanweber.jwttoken.data.ServiceType;
import com.allanweber.jwttoken.helper.JwtConstants;
import com.allanweber.jwttoken.service.JwtTokenParser;
import com.allanweber.jwttoken.service.PublicKeyProvider;
import com.allanweber.jwttoken.service.impl.JwtTokenParserImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JwtTokenParserImplTest {

    @Mock
    PublicKeyProvider publicKeyProvider;

    @Test
    @DisplayName("Given token return token data")
    void tokenData() throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = mockKeyPair();
        String token = Jwts.builder()
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer("issuer")
                .setAudience("audience")
                .setSubject("user_name")
                .setIssuedAt(Date.from(LocalDateTime.of(2022, Month.APRIL, 1, 13, 14, 15).atZone(ZoneId.systemDefault())
                        .toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(360)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .claim(JwtConstants.SCOPES, Collections.singleton("ROLE_USER"))
                .claim(JwtConstants.TENANCY_ID, "tenancy_1")
                .compact();

        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 10L, 10L, "issuer", "audience");
        when(publicKeyProvider.provide(new File("arg"))).thenReturn(keyPair.getPublic());

        JwtTokenParser jwtTokenParser = new JwtTokenParserImpl(publicKeyProvider, properties);
        Jws<Claims> claims = jwtTokenParser.parseToken(token);

        assertThat(claims.getBody().getSubject()).isEqualTo("user_name");
        assertThat(claims.getBody().get(JwtConstants.SCOPES)).isEqualTo(Collections.singletonList("ROLE_USER"));
        assertThat(claims.getBody().get(JwtConstants.TENANCY_ID)).isEqualTo("tenancy_1");
    }

    @Test
    @DisplayName("Given expired token return exception")
    void expiredToken() throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = mockKeyPair();
        String token = Jwts.builder()
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer("issuer")
                .setAudience("audience")
                .setSubject("user_name")
                .setIssuedAt(Date.from(LocalDateTime.of(2022, Month.APRIL, 1, 13, 14, 15).atZone(ZoneId.systemDefault())
                        .toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().minusSeconds(10)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .claim(JwtConstants.SCOPES, Collections.singleton("ROLE_USER"))
                .claim(JwtConstants.TENANCY_ID, "tenancy_1")
                .compact();

        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 10L, 10L, "issuer", "audience");
        when(publicKeyProvider.provide(new File("arg"))).thenReturn(keyPair.getPublic());

        JwtTokenParser jwtTokenParser = new JwtTokenParserImpl(publicKeyProvider, properties);

        JwtException exception = assertThrows(JwtException.class,
                () -> jwtTokenParser.parseToken(token));

        assertThat(exception.getMessage()).contains("Request to parse expired JWT");
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    @DisplayName("Given token signed with other key return exception")
    void invalidSignatureToken() throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = mockKeyPair();
        String token = Jwts.builder()
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setHeaderParam(JwtConstants.HEADER_TYP, JwtConstants.TOKEN_TYPE)
                .setIssuer("issuer")
                .setAudience("audience")
                .setSubject("user_name")
                .setIssuedAt(Date.from(LocalDateTime.of(2022, Month.APRIL, 1, 13, 14, 15).atZone(ZoneId.systemDefault())
                        .toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(360)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .claim(JwtConstants.SCOPES, Collections.singleton("ROLE_USER"))
                .claim(JwtConstants.TENANCY_ID, "tenancy_1")
                .compact();

        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 10L, 10L, "issuer", "audience");

        KeyPair othKeyPair = mockKeyPair();
        when(publicKeyProvider.provide(new File("arg"))).thenReturn(othKeyPair.getPublic());

        JwtTokenParser jwtTokenParser = new JwtTokenParserImpl(publicKeyProvider, properties);

        JwtException exception = assertThrows(JwtException.class,
                () -> jwtTokenParser.parseToken(token));

        assertThat(exception.getMessage()).contains("Request to parse JWT with invalid signature");
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }

    @DisplayName("Given token return MalformedJwtException")
    @Test
    void malformedJwtException() throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = mockKeyPair();
        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 10L, 10L, "issuer", "audience");
        when(publicKeyProvider.provide(new File("arg"))).thenReturn(keyPair.getPublic());
        JwtTokenParser jwtTokenParser = new JwtTokenParserImpl(publicKeyProvider, properties);

        JwtException exception = assertThrows(JwtException.class,
                () -> jwtTokenParser.parseToken("1231231231321"));

        assertThat(exception.getMessage()).contains("Request to parse invalid JWT");
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }

    @DisplayName("Given token return IllegalArgumentException")
    @Test
    void emptyJwt() throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = mockKeyPair();
        JwtProperties properties = new JwtProperties(ServiceType.PROVIDER, new File("arg"), new File("arg"), 10L, 10L, "issuer", "audience");
        when(publicKeyProvider.provide(new File("arg"))).thenReturn(keyPair.getPublic());
        JwtTokenParser jwtTokenParser = new JwtTokenParserImpl(publicKeyProvider, properties);

        JwtException exception = assertThrows(JwtException.class,
                () -> jwtTokenParser.parseToken(""));

        assertThat(exception.getMessage()).contains("Request to parse empty or null JWT");
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }

    private KeyPair mockKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.genKeyPair();
    }
}
