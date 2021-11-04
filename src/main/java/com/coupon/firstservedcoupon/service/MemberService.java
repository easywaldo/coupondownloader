package com.coupon.firstservedcoupon.service;

import com.coupon.firstservedcoupon.dto.ValidMemberRequestDto;
import com.coupon.firstservedcoupon.entity.Member;
import com.coupon.firstservedcoupon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public boolean validUser(ValidMemberRequestDto requestDto) {
        Optional<Member> member = this.memberRepository.findByUserId(requestDto.getUserId());
        if (member.isEmpty()) return false;
        return member.get().getUserPwd().equals(SHAEncryptServiceImpl.getSHA512(requestDto.getUserPwd()));
    }

    public boolean isExistsUser(String userId) {
        Optional<Member> member = this.memberRepository.findByUserId(userId);
        return member.isPresent();
    }
}
