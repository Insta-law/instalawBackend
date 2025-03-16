package com.KnowLaw.backend.Controller;


import com.KnowLaw.backend.Dto.LawyerDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Exception.NotFoundException;
import com.KnowLaw.backend.Service.LawyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/all")
    public ResponseEntity<List<LawyerDto>> getAllLawyers(){
        List<LawyerDto> lawyers = lawyerService.getAllLawyers().stream().map(LawyerDto::new).toList();
        return new ResponseEntity<List<LawyerDto>>(lawyers,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ROLE', 'CONSUMER_ROLE')")
    public ResponseEntity<LawyerDto> findLawyerByLawyerId(@PathVariable UUID id){
        try{
            Lawyer lawyer = lawyerService.findById(id);
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
