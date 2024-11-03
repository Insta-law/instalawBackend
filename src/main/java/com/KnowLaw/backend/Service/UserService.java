package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService{
    @Autowired
    private UserRepository userRepository;


    @Override
    @Transactional
    public User registerUser(SignupRequestDto signupRequest,String hash){
        User user= new User(signupRequest.getEmail(), signupRequest.getUsername(), signupRequest.getPhone(),hash);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

//    @Override
//    public boolean validateLogin(String email, String password){
//        User user=getUserByEmail(email).orElseThrow();
//        return passwordEncoder.matches(password,user.getPasswordHash());
//    }


}
