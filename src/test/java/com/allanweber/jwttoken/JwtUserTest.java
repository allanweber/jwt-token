package com.allanweber.jwttoken;

import com.allanweber.jwttoken.contract.JwtUserData;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class JwtUserTest implements JwtUserData {
    private List<String> userAuthoritiesName;

    private String userEmail;

    private Long userTenancyId;

    private String userTenancyName;
}
