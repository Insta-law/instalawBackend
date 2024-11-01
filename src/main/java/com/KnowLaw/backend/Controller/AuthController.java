package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Model.UserDetails;
import com.KnowLaw.backend.Service.*;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOtpService otpService;
    @Autowired
    private IEmailService emailService;

    @PostMapping("/requestSignup")
    public ResponseEntity<String> RequestSignup(@Valid @RequestBody SignupRequestDto signupRequest){
        if(userService.getUserByUsername(signupRequest.getUsername()).isPresent())
            return new ResponseEntity<String>("Username already exists, choose a different username",HttpStatus.BAD_REQUEST);
        String otp = otpService.generateOtp();
        otpService.storeOtp(signupRequest.getEmail(), otp);
        emailService.sendOtp(signupRequest.getEmail(), otp);
        return new ResponseEntity<String>("OTP sent to your email address. Please verify.", HttpStatus.OK);
    }

    @PostMapping("/finaliseSignup")
    public ResponseEntity<UserDetails> Signup(@RequestBody SignupRequestDto signupRequest , @RequestParam String otp){
        if(userService.getUserByUsername(signupRequest.getUsername()).isPresent())
            return new ResponseEntity<UserDetails>((UserDetails) null,HttpStatus.BAD_REQUEST);

        if(otpService.validateOtp(signupRequest.getEmail(), otp))
        {
            User addedUser = userService.registerUser(signupRequest);
            otpService.clearOtp(signupRequest.getEmail());
            UserDetails userDetails = new UserDetails(addedUser.getId(),addedUser.getEmail(),addedUser.getUsername(),addedUser.getPhone());
            return new ResponseEntity<UserDetails>(userDetails,HttpStatus.CREATED);

        }
        return new ResponseEntity<UserDetails>((UserDetails) null,HttpStatus.FORBIDDEN);
    }


}

