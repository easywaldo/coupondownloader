package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.dto.CouponUserResponseDto;
import com.coupon.firstservedcoupon.entity.CouponDownResultEnum;
import com.coupon.firstservedcoupon.entity.CouponUser;
import com.coupon.firstservedcoupon.entity.TicketingCouponUser;
import com.coupon.firstservedcoupon.repository.CouponCatalogRepository;
import com.coupon.firstservedcoupon.repository.CouponUserRepository;
import com.coupon.firstservedcoupon.repository.MemberRepository;
import com.coupon.firstservedcoupon.repository.TicketingCouponUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final TicketingCouponUserRepository ticketingCouponUserRepository;
    private final CouponCatalogRepository couponCatalogRepository;
    private final CouponUserRepository couponUserRepository;
    private final MemberRepository memberRepository;
    private static final String prefixValue = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Random random = new Random();

    @Autowired
    public CouponService(TicketingCouponUserRepository ticketingCouponUserRepository,
                         MemberRepository memberRepository,
                         CouponCatalogRepository couponCatalogRepository,
                         CouponUserRepository couponUserRepository) {
        this.ticketingCouponUserRepository = ticketingCouponUserRepository;
        this.couponUserRepository = couponUserRepository;
        this.couponCatalogRepository = couponCatalogRepository;
        this.memberRepository = memberRepository;
    }

    public String issueToken(Integer couponId, Long userId) {
        long microTime = System.currentTimeMillis();
        char prefixA =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        char prefixB =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        char prefixC =  prefixValue.charAt(random.nextInt(prefixValue.length()));
        return String.format("%s%s%s%s%s%s", couponId, userId, microTime, prefixA, prefixB, prefixC);
    }

    public void setTicketing(Long userId, String token) {
        var user = new TicketingCouponUser();
        user.setMemberSeq(userId);
        user.setRandomValue(token);
        ticketingCouponUserRepository.save(user);
    }

    @Transactional(readOnly = false, isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public CouponDownResultEnum ticketingCouponUser(
        Instant now,
        Integer couponId,
        Long userId,
        String token) {

        if (now.atZone(ZoneId.of("Asia/Seoul")).getHour() < 13) {
            return CouponDownResultEnum.TIME_UNMATCHED;
        }

        var couponCatalog = couponCatalogRepository.findById(couponId);
        if (couponCatalog.isEmpty()) {
            return CouponDownResultEnum.COUPON_NOT_FOUND;
        }

        if (memberRepository.findById(userId).isEmpty()) {
            return CouponDownResultEnum.MEMBER_NOT_FOUND;
        }

        long ticketingCount = this.ticketingCouponUserRepository.count();
        if (ticketingCount >= 100) {
            return CouponDownResultEnum.ALREADY_FINISHED;
        }

        setTicketing(userId, token);

        CouponUser couponUser = this.couponUserRepository.findByMemberSeqEquals(userId);
        if (couponUser != null) {
            System.out.println("already gave");
            return CouponDownResultEnum.ALREADY_GET;
        }

        var ticketingUser = this.ticketingCouponUserRepository.findById(userId);
        if (ticketingUser.isPresent() && !ticketingUser.get().getRandomValue().equals(token)) {
            System.out.println("different token");
            return CouponDownResultEnum.TOKEN_NOT_MATCHED;
        }

        try {
            couponUserRepository.save(CouponUser.builder()
                .couponSeq(couponId)
                .memberSeq(userId)
                .couponType(couponCatalog.get().getCouponType().name())
                .expiredDate(couponCatalog.get().getExpiredDate())
                .createDate(now.atZone(ZoneOffset.UTC).toLocalDateTime())
                .build());
            Thread.sleep(50L);
            return CouponDownResultEnum.COUPON_DOWNLOADED;
        }
        catch (Exception ex) {
            return CouponDownResultEnum.UNKNOWN_ERROR;
        }

    }

    @Transactional(readOnly = true)
    public List<CouponUserResponseDto> selectCouponUserList(String searchDate) {
        LocalDateTime fromDate = LocalDate.parse(searchDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0);
        LocalDateTime toDate = fromDate.plusDays(1);

        var result = couponUserRepository.findCouponUserByCreateDateBetweenOrderByCouponUserSeqDesc(fromDate, toDate);
        return result.stream().map(x -> CouponUserResponseDto.builder()
            .memberSeq(x.getMemberSeq())
            .couponDownloadedDate(x.getCreateDate())
            .couponType(x.getCouponType())
            .build())
            .collect(Collectors.toList());
    }


}
