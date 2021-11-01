package com.coupon.firstservedcoupon.controller;

import com.coupon.firstservedcoupon.dto.ValidMemberRequestDto;
import com.coupon.firstservedcoupon.service.AuthService;
import com.coupon.firstservedcoupon.service.CookieService;
import com.coupon.firstservedcoupon.service.MemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @Autowired
    public MemberController(MemberService memberService,
                            AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @ApiOperation(value = "회원 로그인", notes = "아이디와 비밀번호로 로그인을 수행한다. 세션에 대한 서버구성은 별도로 하지 않는다")
    @PostMapping("/userLogin")
    public ResponseEntity<Boolean> userLogin(
        @RequestBody ValidMemberRequestDto requestDto,
        @ApiIgnore HttpServletResponse response) throws UnsupportedEncodingException {

        if (!this.memberService.validUser(requestDto)) {
            return ResponseEntity.badRequest().body(false);
        }

        String userJwt = authService.issueToken(requestDto.getUserId());
        if (userJwt.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        response.addCookie(CookieService.addCookie("userJwt", userJwt));

        return ResponseEntity.accepted().body(true);
    }

    @ApiOperation(value = "회원 로그아웃", notes = "회원 로그아웃 세션에 대한 서버구성은 별도로 하지 않는다")
    @PostMapping("/userLogout")
    public ResponseEntity<Boolean> userLogout(@ApiIgnore HttpServletResponse response) {
        Cookie deleteCookie = CookieService.deleteCookie("userJwt");
        response.addCookie(deleteCookie);

        return ResponseEntity.accepted().body(true);
    }
}
