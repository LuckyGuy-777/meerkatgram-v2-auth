package com.meerkatgramv2auth.domain.auth.service;


import com.meerkatgramv2auth.domain.auth.repository.AuthRepository;
import com.meerkatgramv2auth.domain.auth.request.LoginRequestDTO;
import com.meerkatgramv2auth.domain.auth.response.AuthResponseDTO;
import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.cookie.CookieManager;
import com.meerkatgramv2auth.global.errors.custom.InvalidTokenException;
import com.meerkatgramv2auth.global.errors.custom.NotRegisteredException;
import com.meerkatgramv2auth.global.jwt.jwtProvider;
import jakarta.servlet.http.HttpServletRequest;
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

    @Transactional(rollbackFor = Exception.class)
    public AuthResponseDTO reissue(HttpServletRequest request, HttpServletResponse response){
        // 쿠키 리프래시토큰 획득
        String refreshToken = cookieManager.getRefreshTokenToCookie(request)
                .orElseThrow(() -> new InvalidTokenException("리프레시 토큰 없음"));
        
        // 해당 유저의 아이디를 long 으로 파싱 해서 가져옴
        long userId = Long.parseLong(jwtProvider.extractClaims(refreshToken).getSubject());
        
        // 유저 획득 및 가입 여부 확인
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 회원의 토큰입니다."));

        // 비로그인 상태 확인
        // ->  DB 에 리프레시토큰 컬럼이 NULL 인지, 값이 있는지 확인함으로서 비로그인상태 확인
        if(user.getRefreshToken() == null){
            throw new InvalidTokenException("비로그인 상태입니다.");
        }

        // 리프레시 토큰 일치 확인
        // -> 유저가 보낸 리프레시토큰과, 유저에 있는 리프레시토큰이 일치하는지 비교.
        if(!refreshToken.equals(user.getRefreshToken())){
            throw new InvalidTokenException("토큰 불일치 입니다.");
        }

        // 인증정보 생성 및 리턴
        // -> 이 return 까지 오게되면, 문제없는 사용자. 인증생성에, 응답객체와, 유저를 반환함
        return this.generateAuthentication(response, user);

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

    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletResponse response, long userId) {
        // 유저 정보 획득
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않는 회원입니다."));
        
        // DB 에 저장한 리프레시 토큰 파기
        user.setRefreshToken(null);
        authRepository.save(user);

        // Cookie에 저장한 리프레시 토큰 파기
        cookieManager.removeRefreshTokenToCookie(response);
    }
}
