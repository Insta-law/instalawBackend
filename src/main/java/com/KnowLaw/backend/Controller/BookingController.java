package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private IBookingService bookingService;
    @PutMapping("/book")
    @PreAuthorize("hasAnyRole('ADMIN_ROLE', 'CONSUMER_ROLE')")
    public ResponseEntity<String> book(@RequestParam UUID id, @RequestParam LocalDate workingDate,@RequestParam String slot){
        try {
            Booking booking = bookingService.book(id, workingDate, slot);
            return new ResponseEntity<String>(booking.toString(), HttpStatus.OK);
        }catch(NotFoundException ex) {
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.BAD_REQUEST);
        }


    }
}
