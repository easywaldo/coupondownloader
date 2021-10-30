package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.service.CouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @ApiOperation(value = "쿠폰다운로드", notes = "쿠폰다운로드를 요청한다.")
    @Async
    @GetMapping(value = "/download/{couponId}/{userId}")
    public CompletableFuture<?> downloadCoupon(
        @PathVariable Integer couponId, @PathVariable Long userId) {

        if (LocalDateTime.now().getHour() < 13) {
            return CompletableFuture.completedFuture(ResponseEntity.accepted().body(CouponDownResultEnum.TIME_UNMATCHED));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                var token = couponService.issueToken(couponId, userId);
                var result = this.couponService.ticketingCouponUser(couponId, userId, token);
                return ResponseEntity.accepted().body(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "error raised";
        });
    }

    @ApiOperation(value = "다운로드 완료된 회원들에 대한 조회를 수행한다", notes = "")
    @GetMapping(value = "/userCouponList/{yyyyMMdd}/")
    public ResponseEntity<List<CouponUserResponseDto>> selectCouponUserList(@PathVariable String yyyyMMdd) {
        return ResponseEntity.accepted().body(this.couponService.selectCouponUserList(yyyyMMdd));

    }
}
