package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.SlotOpeningDto;
import com.KnowLaw.backend.Service.ISlotService;
import com.KnowLaw.backend.Service.SlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/slot")
public class SlotsController {

    @Autowired
    private ISlotService slotService;
    @PutMapping("/openBatch")
    @PreAuthorize("hasAnyRole('ADMIN_ROLE', 'PROVIDER_ROLE')")
    public ResponseEntity<String> openSlotsBatch(@Valid @RequestBody SlotOpeningDto slots)
    {
        if(slots.getStartDate().isBefore(LocalDate.now()))
            return new ResponseEntity<String>("Start date cannot be in the past",HttpStatus.BAD_REQUEST);
        if(slots.getEndDate().isBefore(slots.getStartDate()))
            return new ResponseEntity<String>("End date must be after start date",HttpStatus.BAD_REQUEST);
        slotService.addSlotsInBatch(slots.getId(),slots.getStartDate(),slots.getEndDate(),slots.getStartTime(),slots.getEndTime());
        return new ResponseEntity<String>("Slots added",HttpStatus.OK);

    }
}
