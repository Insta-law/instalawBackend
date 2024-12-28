package com.KnowLaw.backend.Service;

import java.time.LocalDate;
import java.util.UUID;

public interface ISlotService {
    void addSlotsInBatch(UUID id, LocalDate startDate, LocalDate endDate, String startTime, String endTime);
}
