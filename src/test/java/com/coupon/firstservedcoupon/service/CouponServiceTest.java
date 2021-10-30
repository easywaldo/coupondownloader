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
    public void 동시에_여러_회원이_쿠폰다운로드를_시도할때_정해진_수만큼의_쿠폰만_정상적으로_지급이_되는지_확인한다() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(0, 10000).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 3);

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                var token = this.couponService.issueToken(1, member.longValue());
                this.couponService.ticketingCouponUser(1, member.longValue(), token);
            }));

        // assert
        assertEquals(100, (long) couponUserRepository.findAll().size());
        assertEquals(100, couponUserRepository.findAll().stream().collect(
            Collectors.groupingBy(CouponUser::getMemberSeq)).entrySet().size());
    }

    @Test
    public void 존재하지_않는_쿠폰_아이디로_다운로드를_시도할때_다운로드가_실패한다() {
        // arrange
        var couponId = 9999;
        var memberSeq = 10L;

        // act
        var token = this.couponService.issueToken(couponId, memberSeq);
        var result = this.couponService.ticketingCouponUser(couponId, memberSeq, token);

        // assert
        assertEquals(CouponDownResultEnum.COUPON_NOT_FOUND, result);
    }

    @Test
    public void 존재하지_않는_유저_아이디로_다운로드를_시도할때_다운로드가_실패한다() {
        // arrange
        var couponId = 1;
        var memberSeq = 50000L;

        // act
        var token = this.couponService.issueToken(couponId, memberSeq);
        var result = this.couponService.ticketingCouponUser(couponId, memberSeq, token);

        // assert
        assertEquals(CouponDownResultEnum.MEMBER_NOT_FOUND, result);
    }

    @Test
    public void 이미_쿠폰을_1개_소유하고_있는_아이디로_다운로드를_시도할때_다운로드가_실패한다() {
        // arrange
        var couponId = 1;
        var memberSeq = 1000L;
        this.couponService.ticketingCouponUser(couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // act
        var result = this.couponService.ticketingCouponUser(
            couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_GET, result);
    }

    @Test
    public void 이미_쿠폰을_1개_소유하고_있는_아이디로_기존과_다른_쿠폰을_다운로드를_시도할때_다운로드가_실패한다() {
        // arrange
        var firstCouponId = 1;
        var secondCouponId = 2;
        var memberSeq = 1000L;
        this.couponService.ticketingCouponUser(firstCouponId, memberSeq, this.couponService.issueToken(firstCouponId, memberSeq));

        // act
        var result = this.couponService.ticketingCouponUser(
            firstCouponId, memberSeq, this.couponService.issueToken(secondCouponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_GET, result);
    }

    @Test
    public void 이미_쿠폰이_해당일자에_100개가_소진이_되었다면_쿠폰_다운로드를_시도할때_다운로드가_실패한다() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(1, 101).boxed().collect(Collectors.toList());
        sampleCountList.forEach(x -> {
            var token = this.couponService.issueToken(2, x.longValue());
            this.couponService.ticketingCouponUser(2, x.longValue(), token);
        });
        var couponId = 2;
        var memberSeq = 200L;

        // act
        var result = this.couponService.ticketingCouponUser(
            couponId, memberSeq, this.couponService.issueToken(couponId, memberSeq));

        // assert
        assertEquals(CouponDownResultEnum.ALREADY_FINISHED, result);
    }

    @Test
    public void 쿠폰_동시_다운로드_응모_후_당첨자_리스트를_확인한다() {
        // arrange
        List<Integer> sampleCountList = IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        List<List<Integer>> subList = Lists.partition(sampleCountList, 3);

        // act
        subList.parallelStream().forEach(x ->
            x.forEach(member -> {
                var token = this.couponService.issueToken(2, member.longValue());
                this.couponService.ticketingCouponUser(2, member.longValue(), token);
            }));

        // assert
        var result = couponService.selectCouponUserList("20211030");
        assertEquals(100, result.size());
        assertEquals(100, result.stream()
            .filter(x -> x.getCouponType().equals(CouponTypeEnum.TYPE_B.name())).count());
    }
}