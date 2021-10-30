package com.coupon.firstservedcoupon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUserResponseDto {
    private Long memberSeq;
    private String couponType;
    private LocalDateTime couponDownloadedDate;

    @Builder
    public CouponUserResponseDto(Long memberSeq,
                                 String couponType,
                                 LocalDateTime couponDownloadedDate) {
        this.memberSeq = memberSeq;
        this.couponType = couponType;
        this.couponDownloadedDate = couponDownloadedDate;
    }

}
