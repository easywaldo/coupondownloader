package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.dto.DownloadCouponRequestDto;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.service.AuthService;
import com.coupon.firstservedcoupon.service.CouponService;
import com.coupon.firstservedcoupon.service.MemberService;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
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
    private final AuthService authService;
    private final MemberService memberService;

    @Value("${time.test-mode}")
    public Boolean timeTestMode;

    @Autowired
    public CouponController(CouponService couponService, AuthService authService, MemberService memberService) {
        this.couponService = couponService;
        this.authService = authService;
        this.memberService = memberService;
    }

    @ApiOperation(value = "쿠폰다운로드", notes = "로그인된 회원에 대해서 쿠폰다운로드를 요청한다.\n예시:1 또는 2로 하여 couponId를 입력\n2021-10-30T04:00:00Z와 같은 형식으로 testTime입력\nuserId는 1부터 10000사이의 값을 입력")
    @PostMapping(value = "/download")
    public Mono<ResponseEntity<?>> downloadCoupon(
        HttpServletRequest request,
        @RequestBody DownloadCouponRequestDto requestDto) {

        String jwtString = this.authService.getUserIdFromJwtCookie(request);
        if (Strings.isNullOrEmpty(jwtString)) {
            return Mono.just(ResponseEntity.badRequest().body(CouponDownResultEnum.MEMBER_NOT_LOGIN));
        }
        if (!this.memberService.isExistsUser(jwtString)) {
            return Mono.just(ResponseEntity.badRequest().body(CouponDownResultEnum.MEMBER_NOT_FOUND));
        }

        Mono<ResponseEntity<?>> delayedResult = Mono.defer(() -> {
            //TODO: 주의사항 >> 웹컨트롤러 테스트 하는 경우 testTime 을 전달하지 않았을 때 실제 시간에 따라 실패가 될 수 있으므로 아래 now 변수를 수정해서 진행할 것.
            Instant now = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant();
            if (timeTestMode || !Strings.isNullOrEmpty(requestDto.getTestTime())) {
                Clock clock = Clock.fixed(Instant.parse(requestDto.getTestTime()), ZoneId.of("UTC"));
                now = Instant.now(clock);
            }
            var token = couponService.issueToken(requestDto.getCouponId(), requestDto.getUserId());
            var result = this.couponService.ticketingCouponUser(now, requestDto.getCouponId(), requestDto.getUserId(), token);
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
