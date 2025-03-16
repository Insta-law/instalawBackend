package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.Slots;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Repository.BookingRepository;
import com.KnowLaw.backend.Repository.LawyerRepository;
import com.KnowLaw.backend.Repository.SlotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SlotService implements ISlotService{

    @Autowired
    LawyerRepository lawyerRepository;
    @Autowired
    SlotsRepository slotsRepository;
    @Autowired
    BookingRepository bookingRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int SLOT_DURATION_MINUTES = 60;
    @Override
    public void addSlotsInBatch(UUID id, LocalDate startDate, LocalDate endDate, String startTime, String endTime) {
        if(!lawyerRepository.existsById(id))
            throw new NotFoundException("Lawyer not found");

        LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
        LocalTime end = LocalTime.parse(endTime, TIME_FORMATTER);

        if (start.isAfter(end) || start.equals(end)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (ChronoUnit.MINUTES.between(start, end) < SLOT_DURATION_MINUTES) {
            throw new IllegalArgumentException("Time range must be at least " + SLOT_DURATION_MINUTES + " minutes");
        }

        List<Slots> timeSlots = slotsRepository.fetchSlots(startTime,endTime);
        Lawyer lawyer = lawyerRepository.getReferenceById(id);
        List<Booking> existingBookings = bookingRepository.findByLawyerAndWorkingDateBetween(lawyer, startDate, endDate);
        Set<String> existingSlots = existingBookings.stream()
                .map(booking -> booking.getWorkingDate() + "-" + booking.getSlot().getId())
                .collect(Collectors.toSet());
        List<Booking> newBookings = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            for (Slots slot : timeSlots) {
                String slotKey = currentDate + "-" + slot.getId();
                if (!existingSlots.contains(slotKey)) {
                    newBookings.add(new Booking(
                            lawyer,
                            null,
                            currentDate,
                            slot
                    ));
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if(!newBookings.isEmpty())
            bookingRepository.saveAll(newBookings);

    }

    @Override
    public List<Slots> getAvailableSlots(UUID lawyerId, LocalDate date){
        Lawyer lawyer = lawyerRepository.findById(lawyerId)
                .orElseThrow(() -> new NotFoundException("Lawyer not found"));
        List<Slots> allSlots = slotsRepository.findAll();
        List<Booking> bookings = bookingRepository.findByLawyerAndWorkingDate(lawyerId, date);
        Set<Long> openSlotIds = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.OPEN)
                .map(b -> b.getSlot().getId())
                .collect(Collectors.toSet());
        return allSlots.stream()
                .filter(slot -> openSlotIds.contains(slot.getId()))
                .collect(Collectors.toList());
    }
}
