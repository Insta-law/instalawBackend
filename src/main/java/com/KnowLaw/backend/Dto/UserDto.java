package com.KnowLaw.backend.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String username;
    private String phone;
    private String passwordHash;

}
