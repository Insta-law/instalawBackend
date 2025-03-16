package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.BookingDto;
import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Repository.BookingRepository;
import com.KnowLaw.backend.Repository.LawyerRepository;
import com.KnowLaw.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService implements IBookingService{
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LawyerRepository lawyerRepository;


    @Override
    public Booking book(UUID id, LocalDate workingDate, String slot) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Booking existingSlot = bookingRepository.findByLawyerWorkingDateAndTime(id, workingDate, slot).orElseThrow(()->new NotFoundException("No such booking slot exists"));

        if(existingSlot.getBookedBy() != null)
            throw new NotFoundException("Slot is already booked");

        User user= userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User is not registered"));
        existingSlot.setBookedBy(user);
        existingSlot.setStatus(Booking.BookingStatus.BOOKED);
        return bookingRepository.save(existingSlot);
    }

    @Override
    public Long getCount(UUID id, Booking.BookingStatus status) {
        if(lawyerRepository.findById(id).isEmpty())
            throw new NotFoundException("Lawyer doesn't exists");
        return bookingRepository.countSlots(id,LocalDate.now(),status);
    }

    @Override
    public Long getClents(UUID id)
    {
        if(lawyerRepository.findById(id).isEmpty())
            throw new NotFoundException("Lawyer doesn't exists");
        return bookingRepository.countClients(id);

    }

    @Override
    public List<Booking> getUserBookings(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found"));
        return bookingRepository.findByBookedUser(userId);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new NotFoundException("Booking not found"));
        LocalDate bookingDate = booking.getWorkingDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        LocalTime bookingTime = LocalTime.parse(booking.getSlot().getTime(),formatter);

        LocalDateTime bookingDateTime = LocalDateTime.of(bookingDate,bookingTime);

        LocalDateTime deadline = LocalDateTime.now().plusHours(6);

        if (bookingDateTime.isBefore(deadline)) {
            throw new IllegalStateException("Bookings must be cancelled at least 6 hours in advance");
        }

        booking.setStatus(Booking.BookingStatus.OPEN);
        booking.setBookedBy(null);
        bookingRepository.save(booking);

    }

}
