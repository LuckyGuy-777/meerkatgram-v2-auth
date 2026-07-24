package com.meerkatgramv2auth.global.jwt;


import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.errors.custom.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class jwtProvider {
    // 생성자를 커스터마이징 하려고.
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;


    public jwtProvider(JwtConfig jwtConfig) {

        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.secret()));
    }


    // 토큰 만드는 역할만 따로 가지는 메소드
    public String generateAccessToken(User user){

        return this.generateToken(user, jwtConfig.accessTokenExpiry()); // 만료시간만 다르게 줘서 토큰을 만들고 있따

    }

    public String generateRefreshToken(User user) {
        return this.generateToken(user, jwtConfig.refreshTokenCookieExpiry()); //  만료시간만 다르게 줘서 토큰을 만들고 있다.
    }

    // 외부에서 호출이 되면 안됨 -> private
    // 실제 토큰 만드는 로직
    private String generateToken(User user, int expiry) {
        Date now = new Date(); // 토큰 만드는 시간

        return Jwts.builder()
                // 세팅할 값 세팅
                .header() // 헤더를 세팅하겠다.
                .type(jwtConfig.type()) // 토큰 유형 (이 까지가 헤더)
                .and()
                .subject(String.valueOf(user.getId()))  // subject 세팅 1.
                .issuer(jwtConfig.issuer()) // 이 토큰을 발급하는 사람
                .issuedAt(now) // 토큰 발급 시간
                .expiration(new Date(now.getTime() + expiry)) //2. 기존의 현재시간과, 만료시간을 합쳐서 새로운 date객체 만듬
                .claim("role", user.getRole()) // 프라이빗 클레임. 키와 값으로 설정함
                .signWith(secretKey) // 시그니처 작성. 시그니쳐에, 시크릿 키를 줌
                .compact();
    }


    // 클레임 추출
    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    ;
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("토큰이 만료 되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("서명이 위조된 되었습니다.");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("토큰 형식이 올바르지 않습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("토큰 검증에 실패 했습니다..");
        }

    }
}

// 해시 베이시드 메세지 어우텐티케이션
