package com.KnowLaw.backend.Mapper;

import com.KnowLaw.backend.Dto.UserDto;
import com.KnowLaw.backend.Entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setGoogleId(user.getGoogleId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setGoogleId(userDto.getGoogleId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
