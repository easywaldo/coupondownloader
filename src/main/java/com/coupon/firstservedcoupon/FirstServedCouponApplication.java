package com.coupon.firstservedcoupon;

import com.coupon.firstservedcoupon.entity.CouponCatalog;
import com.coupon.firstservedcoupon.entity.CouponTypeEnum;
import com.coupon.firstservedcoupon.entity.Member;
import com.coupon.firstservedcoupon.repository.CouponCatalogRepository;
import com.coupon.firstservedcoupon.repository.MemberRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@SpringBootApplication
public class FirstServedCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstServedCouponApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
        TicketingCouponUserRepository ticketingCouponUserRepository,
        MemberRepository memberRepository,
        CouponCatalogRepository couponCatalogRepository) {

        return (args) -> {
            ticketingCouponUserRepository.deleteAll();
            couponCatalogRepository.saveAll(List.of(
                CouponCatalog.builder()
                    .couponType(CouponTypeEnum.TYPE_A)
                    .couponCatalogSeq(1)
                    .build(),
                CouponCatalog.builder()
                    .couponType(CouponTypeEnum.TYPE_B)
                    .couponCatalogSeq(2)
                    .build()
            ));
            var memberList = IntStream.range(0, 10001).boxed().collect(Collectors.toList()).stream().map(m -> Member.builder()
                .memberSeq(m.longValue())
                .build()).collect(Collectors.toList());
            memberRepository.saveAll(memberList);
        };
    }

}
