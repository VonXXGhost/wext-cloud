package com.wext.authservice.jwt;

import com.wext.authservice.config.RedisKeyPrefixs;
import com.wext.authservice.service.CustomUserDetailsService;
import com.wext.common.bean.RedisTool;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    private CustomUserDetailsService customUserDetailsService;
    private RedisTool<Date> timeRedisTool;

    @Autowired
    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService, RedisTool<Date> timeRedisTool) {
        this.customUserDetailsService = customUserDetailsService;
        this.timeRedisTool = timeRedisTool;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<String> roles) {
        return _createToken(username, roles, new Date());
    }

    String _createToken(String username, List<String> roles, Date now) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // here changed
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return !claims.getBody().getExpiration().before(new Date());
            var tokenTime = claims.getBody().getExpiration();
            var issuedTime = claims.getBody().getIssuedAt();
            var lastUpdateTime = timeRedisTool.get(RedisKeyPrefixs.lastPasswordUpdatePrefix + getUsername(token));
            return !tokenTime.before(new Date()) && // 有效时间在当前之后
                    (lastUpdateTime == null || !issuedTime.before(lastUpdateTime)); // 签署时间在最后更新时间之后,null时未限制
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Expired or invalid JWT token: " + e);
            return false;
        }
    }
}
