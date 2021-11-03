package com.coupon.firstservedcoupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DownloadCouponRequestDto {
    private Integer couponId;
    private Long userId;
    private String testTime;

    @Builder
    public DownloadCouponRequestDto(Integer couponId,
                                    Long userId,
                                    String testTime) {
        this.couponId = couponId;
        this.userId = userId;
        this.testTime = testTime;
    }
}
