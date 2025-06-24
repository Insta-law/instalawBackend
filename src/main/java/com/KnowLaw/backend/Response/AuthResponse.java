package com.KnowLaw.backend.Response;

import com.KnowLaw.backend.Model.AuthenticatedUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    public AuthenticatedUserDetails userDetails;
    public boolean isSuccess;
    public String errorResponse;

}
