package com.ebs.boardparadice.security;

import com.ebs.boardparadice.model.Gamer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class GamerDetails implements UserDetails {
    private final Gamer gamer;

    public GamerDetails(Gamer gamer) {
        this.gamer = gamer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return gamer.getGamerRoleList().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return gamer.getPassword();
    }

    @Override
    public String getUsername() {
        return gamer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
