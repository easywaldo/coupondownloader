package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.entity.CouponUser;
import com.coupon.firstservedcoupon.entity.TicketingCouponUser;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class CouponService {

    private final TicketingCouponUserRepository ticketingCouponUserRepository;
    private final CouponUserRepository couponUserRepository;

    @Autowired
    public CouponService(TicketingCouponUserRepository ticketingCouponUserRepository,
                         CouponUserRepository couponUserRepository) {
        this.ticketingCouponUserRepository = ticketingCouponUserRepository;
        this.couponUserRepository = couponUserRepository;
    }

    @Transactional(readOnly = false)
    public String ticketingCouponUser(Integer couponId, Long userId) {
        long ticketingCount = this.ticketingCouponUserRepository.count();
        double randomValue = System.currentTimeMillis() + new Random().nextDouble();
        if (ticketingCount >= 100) {
            return "finished";
        }
        CouponUser couponUser = this.couponUserRepository.findByMemberSeqEqualsAndCouponSeqEquals(userId, couponId);
        if (couponUser != null) {
            return "gave";
        }

        var ticketingUser = this.ticketingCouponUserRepository.findById(userId);
        if (ticketingUser.isPresent() && ticketingUser.get().getRandomValue() != randomValue) {
            return "different_random_value";
        }

        try {
            var user = new TicketingCouponUser();
            user.setMemberSeq(userId);
            user.setRandomValue(randomValue);
            this.ticketingCouponUserRepository.save(user);

            couponUserRepository.save(CouponUser.builder()
                .couponSeq(couponId)
                .memberSeq(userId)
                .build());
            return "success";
        }
        catch (Exception ex) {
            return "error";
        }

    }
}
