package com.coupon.firstservedcoupon.repository;

import com.coupon.firstservedcoupon.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
