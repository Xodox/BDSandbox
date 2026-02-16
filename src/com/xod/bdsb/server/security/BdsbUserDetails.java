package com.xod.bdsb.server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * UserDetails with role and optional driverId for DRIVER role.
 */
public class BdsbUserDetails implements UserDetails {

    private final String username;
    private final String passwordHash;
    private final String role;
    private final Integer driverId;

    public BdsbUserDetails(String username, String passwordHash, String role, Integer driverId) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.driverId = driverId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    public String getRole() { return role; }
    public Integer getDriverId() { return driverId; }
}
