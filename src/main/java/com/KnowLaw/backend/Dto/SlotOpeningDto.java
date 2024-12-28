package com.KnowLaw.backend.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotOpeningDto {
    @NotNull
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotBlank
    private String startTime;
    @NotBlank
    private String endTime;
}
