package com.meerkatgramv2auth.domain.user.response;

import com.meerkatgramv2auth.domain.user.entity.User;
import com.meerkatgramv2auth.global.security.constant.ProviderPolicy;
import com.meerkatgramv2auth.global.security.constant.RolePolicy;

import java.time.LocalDateTime;

public record UserResponseDTO(
    // 우리가 필요한 정보만 전달하기 위해 존재함. (비밀번호나 리프레시 토큰 과 같은 중요정보가, 넘어가지 않도록 커스텀함)
    long id
    , String email
    , String nick
    , ProviderPolicy provider
    , RolePolicy role
    , String profile
    , LocalDateTime created_at
) {


    public static UserResponseDTO from(User user) {

        return new UserResponseDTO(
          user.getId()
          ,user.getEmail()
          ,user.getNick()
          ,user.getProvider()
          ,user.getRole()
          ,user.getProfile()
          ,user.getCreatedAt()
        );
    }

}
