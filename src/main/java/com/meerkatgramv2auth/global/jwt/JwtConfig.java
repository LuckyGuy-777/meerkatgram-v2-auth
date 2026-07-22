package com.meerkatgramv2auth.global.jwt;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "jwt") // 경로설정. jwt 필드 안에 속성들을 참조할려고 하는것
public record JwtConfig(
        boolean secure
        ,String issuer
        ,String type
        ,int accessTokenExpiry
        ,int refreshTokenExpiry
        ,String refreshTokenCookieName
        ,int refreshTokenCookieExpiry
        ,String secret
        ,String headerKey
        ,String scheme
        ,String reissueUri

) {
    // yaml 에 있는것들을 가져다 사용할 용도로 있는 파일. .yaml 에, jwt 부분을 참조함



}
