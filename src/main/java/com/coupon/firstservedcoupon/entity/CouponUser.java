package com.coupon.firstservedcoupon.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_user")
@Getter
@NoArgsConstructor
public class CouponUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_user_seq")
    private Integer couponUserSeq;
    @Column(name = "member_seq")
    private Long memberSeq;
    @Column(name = "coupon_seq")
    private Integer couponSeq;
    @Column(name = "expired_date")
    private LocalDateTime expiredDate;
    @CreationTimestamp
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Builder
    public CouponUser(Long memberSeq, Integer couponSeq, LocalDateTime expiredDate) {
        this.memberSeq = memberSeq;
        this.couponSeq = couponSeq;
        this.expiredDate = expiredDate;
    }
}
