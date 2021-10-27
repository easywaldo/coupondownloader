package com.coupon.firstservedcoupon.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    public CouponService couponService;

    @Test
    public void coupon_download_correctly() {

        // act
        var first = this.couponService.ticketingCouponUser(1, 22999L);
        var second = this.couponService.ticketingCouponUser(1, 22999L);
        var third = this.couponService.ticketingCouponUser(1, 22999L);

        // assert
        assertEquals("success", first);
        assertEquals("gave", second);
        assertEquals( "gave", third);


    }

}