package com.allanweber.jwttoken.contract;

import java.util.List;

public interface JwtUser {

    List<String> getAuthorities();

    String getEmail();

    Long getTenancyId();

    String getTenancyName();
}
