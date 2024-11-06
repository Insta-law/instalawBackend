package com.KnowLaw.backend.Mapper;

import com.KnowLaw.backend.Dto.UserDto;
import com.KnowLaw.backend.Entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setPasswordHash(user.getPasswordHash());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole());
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setPhone(userDto.getPhone());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setRole(userDto.getRole());
        return user;
    }
}
