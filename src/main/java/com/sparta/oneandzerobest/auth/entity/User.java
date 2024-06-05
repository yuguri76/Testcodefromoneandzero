package com.sparta.oneandzerobest.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails { // Spring Security의 UserDetails
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String introduction;

    @Column(nullable = false)
    private String statusCode;

    @Column
    private String refreshToken;

    @Column
    private LocalDateTime statusChangeTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public User(String username, String password, String name, String email, String statusCode) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.statusCode = statusCode;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 생성 일자와 업데이트 됐었을때 현재 시간으로 지정
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean isEnabled() {
        return "정상".equals(this.statusCode); // 계정이 활성화된 상태인지 확인
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한 관련 설정
    }
}
