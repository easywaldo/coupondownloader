package com.coupon.firstservedcoupon.repository;

import com.coupon.firstservedcoupon.entity.CouponCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponCatalogRepository extends JpaRepository<CouponCatalog, Integer> {
}
