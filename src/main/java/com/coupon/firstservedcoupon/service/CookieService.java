package com.coupon.firstservedcoupon.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Service
public class CookieService {
    public static Cookie addCookie(
        String cookieName,
        String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // expiration date after 7days
        cookie.setHttpOnly(true);
        return cookie;
    }

    public static Cookie deleteCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public static String getToken(HttpServletRequest request) {
        try {
            String userJwt = Arrays.stream(request.getCookies())
                .filter(x -> x.getName().equals("userJwt"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
            return userJwt;
        }
        catch (Exception e) {
            return "";
        }
    }
}
