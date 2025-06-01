package com.gestionplanillas.application.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gestionplanillas.application.data.User;

public class CustomUserDetail implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail(User user){
        this.username = user.getUsername();
        this.password = user.getHashedPassword();
        this.authorities = user.getRoles().stream().map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return this.authorities;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return this.password;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return this.username;
    }
}
