package com.coupon.firstservedcoupon.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "user_id")
    private String userId;
    @Column(name = "user_pwd")
    private String userPwd;

    @Builder
    public Member(Long memberSeq,
                  String userId,
                  String userPwd) {
        this.memberSeq = memberSeq;
        this.userId = userId;
        this.userPwd = userPwd;
    }
}
