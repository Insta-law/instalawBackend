package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.Booking;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface IBookingService {
    Optional<Booking> book(UUID id, LocalDate workingDate, String slot);
}
