package com.allanweber.jwttoken.service.impl;

import com.allanweber.jwttoken.service.PublicKeyProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
@RequiredArgsConstructor
public class PublicKeyProviderImpl implements PublicKeyProvider {

    private final KeyProvider keyProvider;

    @Override
    public Key provide(File file) throws IOException {
        return keyProvider.loadRsaKey(file,
                "PUBLIC",
                this::publicKeySpec,
                this::publicKeyGenerator);
    }

    private EncodedKeySpec publicKeySpec(String data) {
        return new X509EncodedKeySpec(Base64.decodeBase64(data));
    }

    @SneakyThrows
    private PublicKey publicKeyGenerator(KeyFactory kf, EncodedKeySpec spec) {
        return kf.generatePublic(spec);
    }
}
