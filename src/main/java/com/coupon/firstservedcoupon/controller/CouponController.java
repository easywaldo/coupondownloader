package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
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

import java.time.*;
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

    /***
     *
     * @param couponId
     * @param userId
     * @param testTime : "2021-10-31_140000"
     * @return
     */
    @ApiOperation(value = "쿠폰다운로드", notes = "쿠폰다운로드를 요청한다.")
    @Async
    @GetMapping(value = "/download/{couponId}/{userId}/{testTime}")
    public Mono<ResponseEntity<?>> downloadCoupon(
        @PathVariable Integer couponId, @PathVariable Long userId) {

        return Mono.defer(() -> {
            //TODO: 웹컨트롤러 테스트 하는 경우 시간에 따라 실패가 될 수 있으므로 아래 now 변수를 수정해서 진행할 것.
            Instant now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul")).toInstant();
            var token = couponService.issueToken(couponId, userId);
            var result = this.couponService.ticketingCouponUser(now, couponId, userId, token);
            return Mono.just(ResponseEntity.accepted().body(result));
        });
    }

    @ApiOperation(value = "다운로드 완료된 회원들에 대한 조회를 수행한다", notes = "")
    @GetMapping(value = "/userCouponList/{yyyyMMdd}")
    public Mono<ResponseEntity<List<CouponUserResponseDto>>> selectCouponUserList(@PathVariable String yyyyMMdd) {
        return Mono.just(ResponseEntity.accepted().body(this.couponService.selectCouponUserList(yyyyMMdd)));
    }
}
