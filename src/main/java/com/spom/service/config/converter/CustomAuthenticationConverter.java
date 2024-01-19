package com.spom.service.config.converter;


import com.spom.service.dto.UserInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAuthenticationConverter implements Converter<Jwt, CustomAuthenticationToken> {

    @SuppressWarnings("unchecked")
    @Override
    public CustomAuthenticationToken convert(Jwt source) {
        Map<String, String> user = (Map<String, String>) source.getClaims().get("myuser");
        Object authoritiesClaim = user.get("authorities");
        Set<GrantedAuthority> authorities = ((Collection<String>) authoritiesClaim).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        UserInfo userInfo = new UserInfo(user.get("username"), "", true, true, true, true,
                authorities, user.get("firstName"), user.get("lastName"), user.get("email"),user.get("userId"));
        return new CustomAuthenticationToken(authorities, userInfo);
    }

}

