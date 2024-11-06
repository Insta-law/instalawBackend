package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user= userService
                .getUserByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User with email :"+email+" not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole().getRoleName().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(authority));
    }
}
