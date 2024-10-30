package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.UserDto;
import com.KnowLaw.backend.Entity.User;
import jakarta.transaction.Transactional;

public interface IUserService {
    @Transactional
    User signupOrLoginUser(String googleId, String name, String email);

    UserDto getUserInfo(String googleId);
}
