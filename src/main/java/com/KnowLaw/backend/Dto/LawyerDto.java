package com.KnowLaw.backend.Dto;

import com.KnowLaw.backend.Entity.Lawyer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LawyerDto {
    private UUID uuid;
    private String userName;
    private String governmentId;
    private Double pricing;

    public LawyerDto(Lawyer lawyer){
        uuid = lawyer.getUuid();
        userName = lawyer.getUserName();
        governmentId = lawyer.getGovernmentId();
        pricing = lawyer.getPricing();
    }
}
