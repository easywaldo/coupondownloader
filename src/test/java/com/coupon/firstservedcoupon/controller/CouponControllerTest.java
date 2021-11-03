package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.dto.DownloadCouponRequestDto;
import com.coupon.firstservedcoupon.entity.CouponCatalog;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.entity.CouponTypeEnum;
import com.coupon.firstservedcoupon.entity.Member;
import com.coupon.firstservedcoupon.repository.CouponCatalogRepository;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.coupon.firstservedcoupon.repository.MemberRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import com.coupon.firstservedcoupon.service.AuthService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.io.UnsupportedEncodingException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CouponControllerTest {
    @Autowired
    public WebTestClient webTestClient;

    @Autowired
    public CouponUserRepository couponUserRepository;
    @Autowired
    public TicketingCouponUserRepository ticketingCouponUserRepository;
    @Autowired
    public CouponCatalogRepository couponCatalogRepository;
    @Autowired
    public MemberRepository memberRepository;
    @Autowired
    public AuthService authService;

    @BeforeEach
    public void setUp() {
        webTestClient = webTestClient
            .mutate()
            .responseTimeout(Duration.ofMillis(50000))
            .build();

        this.couponUserRepository.deleteAll();
        this.ticketingCouponUserRepository.deleteAll();
        this.couponCatalogRepository.saveAll(List.of(
            CouponCatalog.builder()
                .couponType(CouponTypeEnum.TYPE_A)
                .couponCatalogSeq(1)
                .build(),
            CouponCatalog.builder()
                .couponType(CouponTypeEnum.TYPE_B)
                .couponCatalogSeq(2)
                .build()
        ));
        var memberList = IntStream.range(0, 10000)
            .boxed()
            .collect(Collectors.toList())
            .stream()
            .map(m -> Member.builder()
                .memberSeq(m.longValue())
                .userId(m.toString())
                .userPwd(m.toString())
                .build()).collect(Collectors.toList());
        memberRepository.saveAll(memberList);
    }

    @Test
    public void 쿠폰_다운로드_웹_요청_테스트() throws UnsupportedEncodingException {
        // arrange
        String instantExpected = "2021-10-30T04:00:00Z";

        // assert
        this.webTestClient.post().uri("/coupon/download")
            .bodyValue(DownloadCouponRequestDto.builder()
                .couponId(1)
                .userId(1L)
                .testTime(instantExpected)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .cookie("userJwt", authService.issueToken("1"))
            .exchange()
            .expectStatus()
            .isAccepted()
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
        List<Integer> sampleCountList = IntStream.range(1, 201).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 4);
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        var searchDate = now.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                try {
                    this.webTestClient.post().uri("/coupon/download")
                        .bodyValue(DownloadCouponRequestDto.builder()
                            .couponId(new Random().nextInt(2))
                            .userId(member.longValue())
                            .testTime(instantExpected)
                            .build())
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie("userJwt", authService.issueToken(member.toString()))
                        .exchange()
                        .expectStatus()
                        .isAccepted();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
            .thenConsumeWhile(x -> {
                assertTrue(List.of(CouponTypeEnum.TYPE_A.name(), CouponTypeEnum.TYPE_B.name()).contains(x.getCouponType()));
                return true;
            })
            .verifyComplete();

    }


}