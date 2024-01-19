package com.spom.service.config.converter;

import com.spom.service.dto.UserInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;
    private final UserInfo userInfo;

    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserInfo userInfo) {
        super(authorities);
        this.userInfo = userInfo;
    }

    @Override
    public boolean isAuthenticated() {
        return !userInfo.getAuthorities().isEmpty();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getName() {
        return userInfo.getUsername();
    }

    @Override
    public Object getPrincipal() {
        return userInfo;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return userInfo.getAuthorities();
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new IllegalArgumentException("Don't do this");
    }

    @Override
    public Object getDetails() {
        return userInfo.getUsername();
    }

}

