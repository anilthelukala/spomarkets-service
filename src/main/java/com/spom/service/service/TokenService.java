package com.spom.service.service;


import com.spom.service.config.JwtUser;
import com.spom.service.dto.UserInfo;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public TokenService(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public String generateToken(Authentication authentication) {
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        UserInfo myUser = (UserInfo) authentication.getPrincipal();
        JwtUser myJwtUser = new JwtUser(myUser.getUsername(), myUser.getFirstName(), myUser.getLastName(), myUser.getFullname(), myUser.getEmail(), myUser.getMobileNo(),
                myUser.getPassword(), myUser.isEnabled(), myUser.isAccountNonExpired(), myUser.isCredentialsNonExpired(),
                myUser.isAccountNonLocked(), authorities);
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .claim("myuser", myJwtUser)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String refreshToken(String token) throws Exception {
        Jwt jwt = decoder.decode(token);
        if (Objects.requireNonNull(jwt.getExpiresAt()).isBefore(Instant.now())) {
            throw new Exception("Token already expired! kindly login again.");
        }
        Map<String, Object> claims = jwt.getClaims();
        Map<String, String> user = (Map<String, String>) claims.get("myuser");
        String userName = user.get("username");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        if (StringUtils.isNotBlank(userName) && userName.equalsIgnoreCase(userInfo.getUsername())) {
            return this.generateToken(authentication);
        } else {
            throw new Exception("Invalid jwt");
        }
    }
}
