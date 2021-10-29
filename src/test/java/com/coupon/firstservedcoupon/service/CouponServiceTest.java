package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.entity.CouponUser;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
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

    @BeforeEach
    public void setUp() {
        this.couponUserRepository.deleteAll();
        this.ticketingCouponUserRepository.deleteAll();
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

}