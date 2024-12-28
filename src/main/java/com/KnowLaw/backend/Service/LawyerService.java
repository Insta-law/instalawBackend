package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Repository.LawyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LawyerService implements ILawyerService{
    @Autowired
    private LawyerRepository lawyerRepository;
    @Override
    public Lawyer registerLawyer(SignupRequestDto signupRequest, User addedUser) {
        Lawyer newLawyer = new Lawyer(signupRequest.getUsername(),signupRequest.getGovtId(),addedUser,0.0);
        return lawyerRepository.save(newLawyer);
    }
}
