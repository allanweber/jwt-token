package com.allanweber.jwttoken.data;

public record ContextUser(String email, Long tenancyId, String tenancyName) {

}
