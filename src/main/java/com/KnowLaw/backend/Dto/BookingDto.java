package com.KnowLaw.backend.Dto;

import com.KnowLaw.backend.Entity.Booking;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.Slots;
import com.KnowLaw.backend.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private UUID id;
    private Lawyer lawyer;
    private User bookedBy;
    private LocalDate workingDate;
    private Slots slot;
    private Booking.BookingStatus status;

    public BookingDto(Booking booking){
        id=booking.getId();
        lawyer=booking.getLawyer();
        bookedBy=booking.getBookedBy();
        workingDate=booking.getWorkingDate();
        slot=booking.getSlot();
        status=booking.getStatus();
    }
}
