package com.KnowLaw.backend.Controller;


import com.KnowLaw.backend.Dto.LawyerDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Service.LawyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/lawyer")
public class LawyerController {

    @Autowired
    private LawyerService lawyerService;

    @GetMapping("/findByUserId")
    @PreAuthorize("hasAnyRole('ADMIN_ROLE', 'CONSUMER_ROLE','PROVIDER_ROLE')")
    ResponseEntity<LawyerDto> findLawyerByUserId(@RequestParam UUID userId){
        try{
            Lawyer lawyer = lawyerService.findByUserID(userId);
            LawyerDto lawyerDto = new LawyerDto(lawyer.getUuid(),lawyer.getUserName(),lawyer.getGovernmentId(),lawyer.getPricing());
            return new ResponseEntity<LawyerDto>(lawyerDto,HttpStatus.OK);
        }
        catch (NotFoundException ex)
        {
            return new ResponseEntity<LawyerDto>((LawyerDto) null, HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex)
        {
            return new ResponseEntity<LawyerDto>((LawyerDto) null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
