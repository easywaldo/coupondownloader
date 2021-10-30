package com.coupon.firstservedcoupon.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_catalog")
@Getter
@NoArgsConstructor
public class CouponCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_catalog_seq")
    private Integer couponCatalogSeq;
    @Enumerated
    @Column(name = "coupon_type")
    private CouponTypeEnum couponType;
    @Column(name = "expired_date")
    private LocalDateTime expiredDate;
    @Column(name = "coupon_amt")
    private Integer couponAmt;
    @Column(name = "coupon_use_limit_amt")
    private Integer couponUseLimitAmt;

    @Builder
    public CouponCatalog(Integer couponCatalogSeq,
                         CouponTypeEnum couponType) {
        this.couponCatalogSeq = couponCatalogSeq;
        this.couponType = couponType;
        this.expiredDate = this.couponType.equals(CouponTypeEnum.TYPE_A) ?
            LocalDateTime.now().plusDays(7) : LocalDateTime.now().plusDays(30);
        this.couponAmt = this.couponType.equals(CouponTypeEnum.TYPE_A) ?
            10000 : 5000;
        this.couponUseLimitAmt = this.couponType.equals(CouponTypeEnum.TYPE_A) ?
            20000 : 5000;

    }
}
