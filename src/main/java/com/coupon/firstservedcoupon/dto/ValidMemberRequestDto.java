package com.coupon.firstservedcoupon.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidMemberRequestDto {
    private String userId;
    private String userPwd;

    @Builder
    public ValidMemberRequestDto(String userId, String userPwd) {
        this.userId = userId;
        this.userPwd = userPwd;
    }
}