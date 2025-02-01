package com.KnowLaw.backend.Dto;

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
}
