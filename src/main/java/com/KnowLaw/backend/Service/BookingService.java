package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService implements IBookingService{
    @Autowired
    private BookingRepository bookingRepository;


    @Override
    public Optional<Booking> book(UUID id, LocalDate workingDate, String slot) {
        Optional<Booking> existingSlot = bookingRepository.findByLawyerWorkingDateAndTime(id, workingDate, slot);
        if(existingSlot.isEmpty())
            throw new NotFoundException("No such booking slot exists");

        if(existingSlot.orElseThrow().getBookedBy() == null)
            throw new NotFoundException("Slot is already booked");

        return bookingRepository.bookSlot(existingSlot.get().getId(), id);
    }
}
