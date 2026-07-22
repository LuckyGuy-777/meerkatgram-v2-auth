package com.meerkatgramv2auth.domain.user.entity;

import com.meerkatgramv2auth.global.security.constant.ProviderPolicy;
import com.meerkatgramv2auth.global.security.constant.RolePolicy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity // 해당 클래스가 JPA 엔티티임을 선언함
@EntityListeners(AuditingEntityListener.class) // 엔티티의 이벤트리스너 지정
@Table(name = "users") // 테이블명 맵핑
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() where id = ?") // soft delete
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nick", nullable = false, length = 20)
    private String nick;

    @Column(name = "provider", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING) // Enum 을 어떤 데이터 형식으로 저장할것인지, 설정하는 어노테이션
    @JdbcTypeCode(Types.VARCHAR) // 자바 엔티티 필드를 데이터베이스의 VARCHAR 로 강제매핑할때 사용
    private ProviderPolicy provider = ProviderPolicy.NONE;

    @Column(name = "role", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    @JdbcTypeCode(Types.VARCHAR)
    private RolePolicy role = RolePolicy.NORMAL;

    @Column(name = "profile", nullable = false, length = 100)
    private String profile;

    @Column(name = "refresh_token", nullable = true, length = 255)
    private String refreshToken; // 서버의 토큰을 재발급 해주기 위한 토큰. db에 저장해 주기로 했으니, 이곳에 존재함

    @CreatedDate // 생성 시, 자동으로 현재시간 입력해줌
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시, 자동으로 시간을 업데이트 해줌
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true) // 빈값 허용함 nullable = true 로 인해.
    private LocalDateTime deletedAt;

}
