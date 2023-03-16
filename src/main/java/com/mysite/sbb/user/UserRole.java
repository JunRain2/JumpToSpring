package com.mysite.sbb.user;

import lombok.Getter;

// 인증후 사용자에게 ADMIN, USER 권한 부여
@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
