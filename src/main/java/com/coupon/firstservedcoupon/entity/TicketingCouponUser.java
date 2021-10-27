package com.coupon.firstservedcoupon.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("ticketing")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TicketingCouponUser implements Serializable {

    private static final long serialVersionUID = 1370692830319429806L;

    @Id
    private Long memberSeq;
    @Indexed
    private double randomValue;
}
