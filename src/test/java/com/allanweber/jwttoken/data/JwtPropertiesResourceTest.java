package com.allanweber.jwttoken.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@EnableConfigurationProperties(JwtProperties.class)
@PropertySource(value = "application-resource.properties")
@ExtendWith(SpringExtension.class)
public class JwtPropertiesResourceTest {

    @Autowired
    JwtProperties properties;

    @DisplayName("JwtProperties for resource service type")
    @Test
    void provider() {
        assertThat(properties.getServiceType()).isEqualTo(ServiceType.RESOURCE);
        assertThat(properties.getPrivateKey()).isNull();
        assertThat(properties.getPublicKey()).isNotNull();
        assertThat(properties.getAccessTokenExpiration()).isNull();
    }
}
