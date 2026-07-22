package com.meerkatgramv2auth.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 인증정보 객체. 어디서 오는 인증정보인지 알기 위해서, 앞에 Header 입력
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    // 각 파라미터 당, 유저가 보내온 정보, 유저에게 보내줄 응답객체, 스프링필터체인을 이용하기 위해 외부에서 받아온 객체
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        // 유저 아이디와, 유저 role 이, 빈문자열 또는 null 이라면 체크함
        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(role)){

            //인증정보 객체 생성.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId
                    ,null // 크레덴셜 정보 (보통, 민감한 정보 넣는 란. 암호화된 정보가 들어감)
                    , List.of(new SimpleGrantedAuthority("ROLE_"+role)) // ROLE 관련 작업할때는, 앞에 ROLE 을 적어야한다함.
            );
            // 스프링 시큐리티에 ,인증정보 넣기
            SecurityContextHolder.getContext().setAuthentication(authentication); // 우리가만든 인증정보 authentication 삽입
        }

        filterChain.doFilter(request,response); // 다음 필터로 넘기는거

    }
}

// 스프링시큐리티에 인증정보 삽입함