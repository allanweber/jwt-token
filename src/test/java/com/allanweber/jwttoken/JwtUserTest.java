package com.allanweber.jwttoken;

import com.allanweber.jwttoken.contract.JwtUser;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class JwtUserTest implements JwtUser {
    private List<String> authorities;

    private String email;

    private Long tenancyId;

    private String tenancyName;
}
