package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CouponControllerTest {
    @Autowired
    public WebTestClient webTestClient;

    @Test
    public void 쿠폰_다운로드_웹_요청_테스트() {
        // assert
        this.webTestClient.get().uri("/coupon/download/1/1")
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


}