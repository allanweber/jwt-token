package com.allanweber.jwttoken.contract;

import java.util.List;

public interface JwtUserData {

    List<String> getUserAuthoritiesName();

    String getUserEmail();

    Long getUserTenancyId();

    String getUserTenancyName();
}
