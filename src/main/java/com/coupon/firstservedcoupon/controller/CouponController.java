package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.service.CouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    @Value("${time.test-mode}")
    public Boolean timeTestMode;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @ApiOperation(value = "쿠폰다운로드", notes = "쿠폰다운로드를 요청한다.")
    @Async
    @GetMapping(value = "/download/{couponId}/{userId}")
    public Mono<ResponseEntity<?>> downloadCoupon(
        @PathVariable Integer couponId, @PathVariable Long userId) {

        if (timeTestMode.equals(false) && LocalDateTime.now().getHour() < 13) {
            return Mono.just(ResponseEntity.accepted().body(CouponDownResultEnum.TIME_UNMATCHED));
        }

        var searchDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        if (couponService.selectCouponUserList(searchDate).size() == 100) {
            return Mono.just(ResponseEntity.badRequest().body(CouponDownResultEnum.ALREADY_FINISHED));
        }

        return Mono.defer(() -> {
            var token = couponService.issueToken(couponId, userId);
            var result = this.couponService.ticketingCouponUser(couponId, userId, token);
            return Mono.just(ResponseEntity.accepted().body(result));
        });
    }

    @ApiOperation(value = "다운로드 완료된 회원들에 대한 조회를 수행한다", notes = "")
    @GetMapping(value = "/userCouponList/{yyyyMMdd}")
    public Mono<ResponseEntity<List<CouponUserResponseDto>>> selectCouponUserList(@PathVariable String yyyyMMdd) {
        return Mono.just(ResponseEntity.accepted().body(this.couponService.selectCouponUserList(yyyyMMdd)));
    }
}
