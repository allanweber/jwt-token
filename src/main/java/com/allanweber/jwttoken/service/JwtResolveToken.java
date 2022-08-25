package com.allanweber.jwttoken.service;

import javax.servlet.http.HttpServletRequest;

public interface JwtResolveToken {

    String resolve(HttpServletRequest request);

}
