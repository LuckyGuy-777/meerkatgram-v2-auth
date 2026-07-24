package com.meerkatgramv2auth.global.cookie;

import com.meerkatgramv2auth.global.jwt.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;


// 공통모듈 방식 으로 제작한다고함

@Component
@RequiredArgsConstructor
public class CookieManager {

    private final JwtConfig jwtConfig;

    // 서비스에는 얘만 호출함 (외부에서 호출할때 사용하는 용도)
    public void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken){
        this.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , refreshToken
                , jwtConfig.refreshTokenCookieExpiry()
                , jwtConfig.reissueUri()
                // 쿠키 객체
                // 쿠키이름 가져옴
                // 리프레시토큰 가져오고
                // 리프레시토큰 만료시간을 가져옴
                // 어떤 이슈에서 실행시킬 패턴인가.
        );
    }

    // 쿠키에서 리프레시토큰 제거.
    public void removeRefreshTokenToCookie(HttpServletResponse response) {
        this.setCookie(
                response
                , jwtConfig.refreshTokenCookieName()
                , null
                , 0
                , jwtConfig.reissueUri()

                //
                // 쿠키이름 가져옴
                // 리프레시토큰 을 null 로만듬
                // 리프레시토큰 만료시간을 가져옴
                // 어떤 이슈에서 실행시킬 패턴인가.
        );
    }

    public Optional<String> getRefreshTokenToCookie(HttpServletRequest request) {
        return this.getCookie(request, jwtConfig.refreshTokenCookieName())
                .map(Cookie::getValue); // 해당 쿠키의 값만 Optional 로 가져옴
    }


    // Request Header 에서 특정 쿠키를 획득 ( Optinal 반환)
    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {

        // 데이터가 없는 패턴
        // 쿠키 존재여부 확인
        if(request.getCookies() == null) {
            return Optional.empty();
        }

        // 데이터가 있는 패턴
        // name 에 일치하는 쿠키 획득
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    // 쿠키 생성 메서드
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, String path){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(jwtConfig.secure());

        response.addCookie(cookie);
    }
}


/*
*  # 쿠키 생성 파트
*
*  setCookie 는 쿠키를 생성하는 메소드.
*
*  HttpServletResponse response 으로 쿠키를 가져오고,
*
*   String name  쿠키 이름을 가져옴,
*
*  String value  값을 가져옴 ,
*
*  int maxAge  쿠키의 유효기간을 가져오는듯함
*
* , String path 쿠키의 요청경로를 받아오는듯함
*   path 로 요청경로를 받아옴
*
*
*
*
* Cookie cookie = new Cookie(name, value);
* 해당 이름과 값으로 쿠키 인스턴스를 만듬
*
* cookie.setPath(path); 쿠키를 사용할 경로 설정
*
* cookie.setMaxAge(maxAge); 쿠키 유효시간 설정
*
* cookie.setHttpOnly(true); http Only 설정 XSS 공격방지 설정
*
* cookie.setSecure(jwtConfig.secure());  시큐어 설정( MITM 공격 방지)
*
* MITM 은, 클라이언트가, 백엔드와 통신하는 과정 중에, 정보를 탈취해서
* 확인을 하는 방법.  https 를 사용해서 방지함
*
* response.addCookie(cookie); 쿠키를 response 객체에 담는 라인
*  response 객체에 우리가 전달할 쿠키를 세팅하는 코드.
*
* response는, 클라이언트에게 리턴될 객체
*
*
*  # 쿠키 획득 파트
* -------------------------------
*
*
* @RequiredArgsConstructor
* 필드만 만들어도 해당 생성자를 자동생성
* 클래스래벨에서 선언
*
* @Component
* 빈 등록하는것. 클래스래벨에서 선언
*
*
*
*
* - getCookie 메소드는..  ( request, String name )
* 리퀘스트로 쿠키를 받아오는데, 쿠키가 없으면 null 을 반환함
*
* Optional 객체는, null 처리를 해줘야함
* null 을 반환할 여지가 있는 객체는, Optional 으로 감싸줘야함
*
* request.getCookies() 하면, 쿠키배열형식으로 쿠키 전체를 가져옴.
*
* Optional.empty(); 데이터가 없다고 하는 의미의 메소드
*
* Arrays.stream(request.getCookies()) 쿠키배열을 스트림으로 만듬
* 스트림으로 반복문 처리를 함
*
* cookie -> cookie.getName().equals(name))
* 쿠키에서 문자열 형식으로, name과 같은이름을 가져옴
*
*
* .findFirst() 일치하는것 하나만 가져옴
*  있을수도 없을수도 있다는걸 가장하기에, 리턴타입은, Optional
*
* */