package com.meerkatgramv2auth.domain.auth.service;


import com.meerkatgramv2auth.domain.auth.repository.AuthRepository;
import com.meerkatgramv2auth.domain.auth.request.LoginRequestDTO;
import com.meerkatgramv2auth.domain.auth.response.AuthResponseDTO;
import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.cookie.CookieManager;
import com.meerkatgramv2auth.global.errors.custom.NotRegisteredException;
import com.meerkatgramv2auth.global.jwt.jwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final jwtProvider jwtProvider;
    private final CookieManager cookieManager;

    @Transactional(rollbackFor = Exception.class) // 모든 exception의 발생 에 대해 롤백
    public AuthResponseDTO login(HttpServletResponse response, LoginRequestDTO loginRequestDTO) {

        // 유저정보 획득 & 가입여부 확인 (db 조회 해서 받는값)
        User user = authRepository.findByEmail(loginRequestDTO.email())
                .orElseThrow(() -> new NotRegisteredException("아이디와 비밀번호를 입력해주세요"));

        // 비밀번호 체크
        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new NotRegisteredException("d아이디 비밀번호를 확인해주세요");
        }

        return this.generateAuthentication(response,user);   // 컨트롤러에게 전달
    }

    private AuthResponseDTO generateAuthentication(HttpServletResponse response, User user){
        //토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);


        // 리프래시토큰 DB 저장 처리
        user.setRefreshToken(refreshToken);
        authRepository.save(user); // 유저엔티티 저장

        // 리프레시토큰 쿠키에 저장
        cookieManager.setRefreshTokenToCookie(response, refreshToken);

        return AuthResponseDTO.from(user, accessToken);
    }
}
