package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.User;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface IUserService {
    /*User registerUser(SignupRequestDto signupRequest);*/

    @Transactional
    User registerUser(SignupRequestDto signupRequest, String hash);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

//    boolean validateLogin(String email, String password);
    /*@Transactional
    User signupOrLoginUser(String googleId, String name, String email);

    UserDto getUserInfo(String googleId);*/
}
