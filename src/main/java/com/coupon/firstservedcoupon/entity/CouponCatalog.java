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
    @Column(name = "coupon_type")
    private CouponTypeEnum couponType;
    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Builder
    public CouponCatalog(Integer couponCatalogSeq,
                         CouponTypeEnum couponType) {
        this.couponCatalogSeq = couponCatalogSeq;
        this.couponType = couponType;
        this.expiredDate = this.couponType.equals(CouponTypeEnum.TYPE_A) ?
            LocalDateTime.now().plusDays(10) : LocalDateTime.now().plusDays(20);
    }
}
