package com.coupon.firstservedcoupon.service;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${auth.token.key}")
    private String secretKey;

    public String issueToken(String userId) throws UnsupportedEncodingException {

        String jwt = Jwts.builder()
                .setSubject(this.secretKey)
                .setExpiration(Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant()))
                .claim("name", userId)
                .claim("scope", "coupondownload/member")
                .signWith(
                        SignatureAlgorithm.HS256,
                        "secret".getBytes("UTF-8")
                )
                .compact();
        return jwt;
    }

    public Map<String, String> validateToken(String token) {

        Map<String, String> result = new HashMap<String, String>();

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("secret".getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();

            String name = claims.get("name", String.class);
            String scope = claims.get("scope", String.class);

            result.put("name", name);
            result.put("scope", scope);

            return result;
        }
        catch (Exception e) {
            result.put("name", "");
            result.put("scope", "");
            return result;
        }
    }

    public String getUserIdFromJwtCookie(HttpServletRequest request) {
        String userJwt = CookieService.getToken(request);
        if (Strings.isNullOrEmpty(userJwt)) {
            return "";
        }
        Map<String, String> userInfo = this.validateToken(userJwt);
        return userInfo.get("name");
    }
}
