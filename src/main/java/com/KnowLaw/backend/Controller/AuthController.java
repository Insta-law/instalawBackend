package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Service.EmailService;
import com.KnowLaw.backend.Service.OtpService;
import com.KnowLaw.backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/requestSignup")
    public ResponseEntity<String> RequestSignup(@Valid @RequestBody SignupRequestDto signupRequest){
        String otp = otpService.generateOtp();
        otpService.storeOtp(signupRequest.getEmail(), otp);
        emailService.sendOtp(signupRequest.getEmail(), otp);
        return new ResponseEntity<String>("OTP sent to your email address. Please verify.", HttpStatus.OK);
    }
}

