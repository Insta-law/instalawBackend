package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.User;

import java.util.Optional;

public interface IUserService {
    User registerUser(SignupRequestDto signupRequest);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);
    /*@Transactional
    User signupOrLoginUser(String googleId, String name, String email);

    UserDto getUserInfo(String googleId);*/
}
