package com.coupon.firstservedcoupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DownloadCouponRequestDto {
    private Integer couponId;
    private String testTime;

    @Builder
    public DownloadCouponRequestDto(Integer couponId,
                                    String testTime) {
        this.couponId = couponId;
        this.testTime = testTime;
    }
}
