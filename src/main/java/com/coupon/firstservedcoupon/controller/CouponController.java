package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.service.CouponService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CouponService couponService;
    @Value("${time.test-mode}")
    public Boolean timeTestMode;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @ApiOperation(value = "쿠폰다운로드", notes = "쿠폰다운로드를 요청한다.")
    @GetMapping(value = "/download/{couponId}/{userId}/{testTime}")
    public Mono<ResponseEntity<?>> downloadCoupon(
        @PathVariable Integer couponId, @PathVariable Long userId, @PathVariable String testTime) {

        Mono<ResponseEntity<?>> delayedResult = Mono.defer(() -> {
            //TODO: 주의사항 >> 웹컨트롤러 테스트 하는 경우 시간에 따라 실패가 될 수 있으므로 아래 now 변수를 수정해서 진행할 것.
            Instant now = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant();
            if (timeTestMode) {
                Clock clock = Clock.fixed(Instant.parse(testTime), ZoneId.of("UTC"));
                now = Instant.now(clock);
            }
            var token = couponService.issueToken(couponId, userId);
            var result = this.couponService.ticketingCouponUser(now, couponId, userId, token);
            logger.info("completed : " + result.name());
            return Mono.just(ResponseEntity.accepted().body(result));
        });
        logger.info("processing download coupon ....");
        return delayedResult;
    }

    @ApiOperation(value = "다운로드 완료된 회원들에 대한 조회를 수행한다", notes = "다운로드 완료된 회원들에 대한 조회를 수행한다")
    @GetMapping(value = "/userCouponList/{yyyyMMdd}")
    public Mono<ResponseEntity<List<CouponUserResponseDto>>> selectCouponUserList(@PathVariable String yyyyMMdd) {
        return Mono.just(ResponseEntity.accepted().body(this.couponService.selectCouponUserList(yyyyMMdd)));
    }
}
