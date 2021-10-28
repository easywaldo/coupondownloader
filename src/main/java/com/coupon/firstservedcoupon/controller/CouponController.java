package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.service.CouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @ApiOperation(value = "쿠폰다운로드", notes = "쿠폰다운로드를 요청한다.")
    @Async
    @GetMapping(value = "/downloadCoupon/{couponId}/{userId}")
    public CompletableFuture<String> downloadCoupon(
        @PathVariable Integer couponId, @PathVariable Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var token = couponService.issueToken(couponId, userId);
                return this.couponService.ticketingCouponUser(couponId, userId, token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "error raised";
        });
    }
}
