package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user= userService.getUserByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User with email :"+email+" not found"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPasswordHash(),new ArrayList<>());
    }
}
