package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Dto.UserDto;
import com.KnowLaw.backend.Entity.User;
import jakarta.transaction.Transactional;

public interface IUserService {
    void registerUser(SignupRequestDto signupRequest);
    /*@Transactional
    User signupOrLoginUser(String googleId, String name, String email);

    UserDto getUserInfo(String googleId);*/
}
