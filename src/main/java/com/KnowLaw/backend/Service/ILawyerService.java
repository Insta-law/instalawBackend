package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.User;

import java.util.UUID;

public interface ILawyerService {
    Lawyer registerLawyer(SignupRequestDto signupRequest, User addedUser);

    Lawyer findByUserID(UUID userId);
}
