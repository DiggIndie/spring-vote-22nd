package com.diggindie.vote.common.config.security;

import com.diggindie.vote.common.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String externalId;
    private final Role role;

    public CustomUserDetails(Long memberId, String externalId, Role role) {
        this.memberId = memberId;
        this.externalId = externalId;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return externalId;
    }

    public String getExternalId() { return externalId; }

    public Long getMemberId() {
        return memberId;
    }

    @Override public String getPassword() {
        return null;
    }

    @Override public boolean isAccountNonExpired() {
        return true;
    }

    @Override public boolean isAccountNonLocked() {
        return true;
    }

    @Override public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override public boolean isEnabled() {
        return true;
    }
}
