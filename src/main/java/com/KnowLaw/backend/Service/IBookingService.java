package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBookingService {
    Booking book(UUID id, LocalDate workingDate, String slot);

    Long getCount(UUID id, Booking.BookingStatus status);

    Long getClents(UUID id);

    List<Booking> getUserBookings(UUID userId);

    void cancelBooking(UUID id);
}
