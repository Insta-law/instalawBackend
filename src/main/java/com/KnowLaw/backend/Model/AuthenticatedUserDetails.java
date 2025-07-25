package com.KnowLaw.backend.Model;

import com.KnowLaw.backend.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserDetails {
    private UUID id;
    private String email;
    private String username;
    private String phone;
    private Role role;
}
