package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.entity.CouponTypeEnum;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CouponControllerTest {
    @Autowired
    public WebTestClient webTestClient;

    @Autowired
    public CouponUserRepository couponUserRepository;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient.mutate().responseTimeout(Duration.ofMillis(50000)).build();
    }

    @Test
    public void 쿠폰_다운로드_웹_요청_테스트() {
        // arrange
        String instantExpected = "2021-10-30T04:00:00Z";

        // assert
        this.webTestClient.get().uri("/coupon/download/1/1/"+ instantExpected)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isAccepted()
            .returnResult(CouponDownResultEnum.class)
            .getResponseBody()
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertEquals(item.name(), CouponDownResultEnum.COUPON_DOWNLOADED.name());
                return true;
            }).verifyComplete();
    }

    @Test
    public void 쿠폰_다운로드_웹_동시_요청_테스트() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(1, 501).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 4);
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        var searchDate = now.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                this.webTestClient.get().uri("/coupon/download/1/1/" + instantExpected)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isAccepted();
            }));

        // assert
        this.webTestClient.get().uri("/coupon/userCouponList/"+searchDate)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isAccepted()
            .returnResult(CouponUserResponseDto.class)
            .getResponseBody()
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertEquals(CouponTypeEnum.TYPE_A.name(), item.getCouponType());
                return true;
            }).verifyComplete();

    }


}