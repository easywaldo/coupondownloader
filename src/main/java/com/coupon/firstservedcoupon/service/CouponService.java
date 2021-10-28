package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.entity.CouponUser;
import com.coupon.firstservedcoupon.entity.TicketingCouponUser;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class CouponService {

    private final TicketingCouponUserRepository ticketingCouponUserRepository;
    private final CouponUserRepository couponUserRepository;
    private static final String prefixValue = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Random random = new Random();

    @Autowired
    public CouponService(TicketingCouponUserRepository ticketingCouponUserRepository,
                         CouponUserRepository couponUserRepository) {
        this.ticketingCouponUserRepository = ticketingCouponUserRepository;
        this.couponUserRepository = couponUserRepository;
    }

    public String issueToken(Integer couponId, Long userId) {
        long microTime = System.currentTimeMillis();
        char prefixA =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        char prefixB =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        char prefixC =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        return String.format("%s%s%s%s%s%s", couponId, userId, microTime, prefixA, prefixB, prefixC);
    }

    public void setTicketing(Integer couponId, Long userId, String token) {
        var user = new TicketingCouponUser();
        user.setMemberSeq(userId);
        user.setRandomValue(token);
        ticketingCouponUserRepository.save(user);
    }
    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public String ticketingCouponUser(Integer couponId, Long userId, String token) {
        long ticketingCount = this.ticketingCouponUserRepository.count();

        if (ticketingCount >= 100) {
            return "finished";
        }

        setTicketing(couponId, userId, token);

        CouponUser couponUser = this.couponUserRepository.findByMemberSeqEqualsAndCouponSeqEquals(userId, couponId);
        if (couponUser != null) {
            System.out.println("already gave");
            return "gave";
        }

        var ticketingUser = this.ticketingCouponUserRepository.findById(userId);
        if (ticketingUser.isPresent() && !ticketingUser.get().getRandomValue().equals(token)) {
            System.out.println("different token");
            return "different_random_value";
        }

        try {
            couponUserRepository.save(CouponUser.builder()
                .couponSeq(couponId)
                .memberSeq(userId)
                .build());
            Thread.sleep(50L);
            return "success";
        }
        catch (Exception ex) {
            return "error";
        }

    }
}
