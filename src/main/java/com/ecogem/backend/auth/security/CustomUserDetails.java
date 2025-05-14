package com.ecogem.backend.auth.security;

import com.ecogem.backend.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: ROLE_COMPANY_WORKER 또는 ROLE_STORE_OWNER
        String roleName = "ROLE_" + user.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override public String getUsername()                { return user.getLoginId(); }
    @Override public String getPassword()                { return user.getPwd();     }
    @Override public boolean isAccountNonExpired()       { return true; }
    @Override public boolean isAccountNonLocked()        { return true; }
    @Override public boolean isCredentialsNonExpired()   { return true; }
    @Override public boolean isEnabled()                 { return true; }
}
