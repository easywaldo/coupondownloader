package com.coupon.firstservedcoupon.repository;

import com.coupon.firstservedcoupon.entity.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {
    CouponUser findByMemberSeqEqualsAndCouponSeqEquals(Long memberSeq, Integer couponSeq);
    CouponUser findByMemberSeqEquals(Long memberSeq);
    List<CouponUser> findCouponUserByCreateDateBetweenOrderByCouponUserSeqDesc(LocalDateTime start, LocalDateTime end);
}
