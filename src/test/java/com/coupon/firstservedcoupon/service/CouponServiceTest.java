package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.entity.*;
import com.coupon.firstservedcoupon.repository.CouponCatalogRepository;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.coupon.firstservedcoupon.repository.MemberRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    public CouponService couponService;
    @Autowired
    public CouponUserRepository couponUserRepository;
    @Autowired
    public TicketingCouponUserRepository ticketingCouponUserRepository;
    @Autowired
    public CouponCatalogRepository couponCatalogRepository;
    @Autowired
    public MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {

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
        var memberList = IntStream.range(0, 10000).boxed().collect(Collectors.toList()).stream().map(m -> Member.builder()
            .memberSeq(m.longValue())
            .build()).collect(Collectors.toList());
        memberRepository.saveAll(memberList);
    }

    @RepeatedTest(value = 10)
    public void ?????????_??????_?????????_?????????????????????_????????????_?????????_????????????_?????????_???????????????_?????????_?????????_????????????() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(0, 10000).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 3);
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                var token = this.couponService.issueToken(1, member.longValue());
                this.couponService.ticketingCouponUser(now,1, member.longValue(), token);
            }));

        // assert
        assertEquals(100, (long) couponUserRepository.findAll().size());
        assertEquals(100, couponUserRepository.findAll().stream().collect(
            Collectors.groupingBy(CouponUser::getMemberSeq)).entrySet().size());
    }

    @Test
    public void ????????????_??????_??????_????????????_???????????????_????????????_???????????????_????????????() {
        // arrange
        var couponId = 9999;
        var memberSeq = 10L;
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);

        // act
        var token = this.couponService.issueToken(couponId, memberSeq);
        var result = this.couponService.ticketingCouponUser(now, couponId, memberSeq, token);

        // assert
        assertEquals(CouponDownResultEnum.COUPON_NOT_FOUND, result);
    }

    @Test
    public void ????????????_??????_??????_????????????_???????????????_????????????_???????????????_????????????() {
        // arrange
        var couponId = 1;
        var memberSeq = 50000L;
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);

        // act
        var token = this.couponService.issueToken(couponId, memberSeq);
        var result = this.couponService.ticketingCouponUser(now, couponId, memberSeq, token);

        // assert
        assertEquals(CouponDownResultEnum.MEMBER_NOT_FOUND, result);
    }

    @Test
    public void ????????????_????????????_??????_?????????_???????????????_????????????_???????????????_????????????() {
        // arrange
        var couponId = 1;
        var memberSeq = 1L;
        String instantExpected = "2021-10-30T02:59:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);

        // act
        var token = this.couponService.issueToken(couponId, memberSeq);
        var result = this.couponService.ticketingCouponUser(now, couponId, memberSeq, token);

        // assert
        assertEquals(CouponDownResultEnum.TIME_UNMATCHED, result);
    }

    @Test
    public void ??????_?????????_1???_????????????_??????_????????????_???????????????_????????????_???????????????_????????????() {
        // arrange
        var couponId = 1;
        var memberSeq = 1000L;
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        this.couponService.ticketingCouponUser(now, couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // act
        var result = this.couponService.ticketingCouponUser(
            now, couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_GET, result);
    }

    @Test
    public void ??????_?????????_1???_????????????_??????_????????????_?????????_??????_?????????_???????????????_????????????_???????????????_????????????() {
        // arrange
        var firstCouponId = 1;
        var secondCouponId = 2;
        var memberSeq = 1000L;
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        this.couponService.ticketingCouponUser(now, firstCouponId, memberSeq, this.couponService.issueToken(firstCouponId, memberSeq));

        // act
        var result = this.couponService.ticketingCouponUser(
            now, firstCouponId, memberSeq, this.couponService.issueToken(secondCouponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_GET, result);
    }

    @Test
    public void ??????_?????????_???????????????_100??????_?????????_????????????_??????_???????????????_????????????_???????????????_????????????() {
        // arrange
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        List<Integer> sampleCountList = IntStream.range(1, 101).boxed().collect(Collectors.toList());
        sampleCountList.forEach(x -> {
            var token = this.couponService.issueToken(2, x.longValue());
            this.couponService.ticketingCouponUser(now,2, x.longValue(), token);
        });
        var couponId = 2;
        var memberSeq = 200L;


        // act
        var result = this.couponService.ticketingCouponUser(
            now, couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_FINISHED, result);
    }

    @Test
    public void ??????_??????_????????????_??????_???_?????????_????????????_????????????() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 3);
        String instantExpected = "2021-10-30T04:00:00Z";
        Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
        Instant now = Instant.now(clock);
        var searchDate = now.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                var token = this.couponService.issueToken(2, member.longValue());
                this.couponService.ticketingCouponUser(now,2, member.longValue(), token);
            }));

        // assert
        var result = couponService.selectCouponUserList(searchDate);
        assertEquals(100, result.size());
        assertEquals(100, result.stream()
            .filter(x -> x.getCouponType().equals(CouponTypeEnum.TYPE_B.name())).count());
    }
}