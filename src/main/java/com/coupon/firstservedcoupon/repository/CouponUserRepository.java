package com.coupon.firstservedcoupon.repository;

import com.coupon.firstservedcoupon.entity.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {
    CouponUser findByMemberSeqEqualsAndCouponSeqEquals(Long memberSeq, Integer couponSeq);
}
