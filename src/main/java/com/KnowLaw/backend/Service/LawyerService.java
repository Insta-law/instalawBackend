package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Repository.LawyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LawyerService implements ILawyerService {
    @Autowired
    private LawyerRepository lawyerRepository;

    @Override
    public Lawyer registerLawyer(SignupRequestDto signupRequest, User addedUser) {
        Lawyer newLawyer = new Lawyer(signupRequest.getUsername(), signupRequest.getGovtId(), addedUser, 0.0);
        return lawyerRepository.save(newLawyer);
    }

    @Override
    public Lawyer findByUserID(UUID userId) {
        return lawyerRepository.findLawyerByUserId(userId).orElseThrow(() -> new NotFoundException("Lawyer not found"));
    }

    @Override
    public List<Lawyer> getAllLawyers(){
        return lawyerRepository.findAll();
    }

    @Override
    public Lawyer findById(UUID id){
        return lawyerRepository.findById(id).orElseThrow(() -> new NotFoundException("Lawyer not found"));
    }
}
