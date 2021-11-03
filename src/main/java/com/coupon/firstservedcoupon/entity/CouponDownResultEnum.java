package com.coupon.firstservedcoupon.entity;

public enum CouponDownResultEnum {
    ALREADY_FINISHED(1000),
    TIME_UNMATCHED(2000),
    ALREADY_GET(3000),
    TOKEN_NOT_MATCHED(4000),
    COUPON_DOWNLOADED(0),
    COUPON_NOT_FOUND(5000),
    MEMBER_NOT_FOUND(6000),
    MEMBER_NOT_LOGIN(7000),
    UNKNOWN_ERROR(9999);

    private final Integer value;

    CouponDownResultEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return this.value; }


}
