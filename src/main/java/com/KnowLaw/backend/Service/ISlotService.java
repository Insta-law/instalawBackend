package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.Slots;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ISlotService {
    void addSlotsInBatch(UUID id, LocalDate startDate, LocalDate endDate, String startTime, String endTime);

    List<Slots> getAvailableSlots(UUID lawyerId, LocalDate date);
}
