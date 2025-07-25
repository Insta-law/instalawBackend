package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Exception.UnauthorizedException;
import com.KnowLaw.backend.Model.AuthenticatedUserDetails;
import com.KnowLaw.backend.Response.AuthResponse;
import com.KnowLaw.backend.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IOtpService otpService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ILawyerService lawyerService;

    @PostMapping("/requestSignup")
    public ResponseEntity<String> RequestSignup(@Valid @RequestBody SignupRequestDto signupRequest){
        if(userService.getUserByEmail(signupRequest.getEmail()).isPresent())
            return new ResponseEntity<String>("Email already exists, choose a different email",HttpStatus.BAD_REQUEST);
        if(userService.getUserByUsername(signupRequest.getUsername()).isPresent())
            return new ResponseEntity<String>("Username already exists, choose a different username",HttpStatus.BAD_REQUEST);
        sendOtp(signupRequest);
        return new ResponseEntity<String>("OTP sent to your email address. Please verify.", HttpStatus.OK);
    }

    private void sendOtp(SignupRequestDto signupRequest) {
        String otp = otpService.generateOtp();
        otpService.storeOtp(signupRequest.getEmail(), otp);
        emailService.sendOtp(signupRequest.getEmail(), otp);
    }

    @PostMapping("/finaliseSignup")
    public ResponseEntity<AuthResponse> Signup(@RequestBody SignupRequestDto signupRequest , @RequestParam String otp){
        if(userService.getUserByEmail(signupRequest.getEmail()).isPresent())
            return new ResponseEntity<AuthResponse>(new AuthResponse(null,false,"Email already exists"),HttpStatus.BAD_REQUEST);
        if(userService.getUserByUsername(signupRequest.getUsername()).isPresent())
            return new ResponseEntity<AuthResponse>(new AuthResponse(null,false,"Username already exists"),HttpStatus.BAD_REQUEST);

        if(otpService.validateOtp(signupRequest.getEmail(), otp)) {
            return registerUser(signupRequest);

        }

        return new ResponseEntity<AuthResponse>(new AuthResponse(null,false,"Incorrect otp"),HttpStatus.FORBIDDEN);
    }

    @NotNull
    @Transactional
    private ResponseEntity<AuthResponse> registerUser(SignupRequestDto signupRequest) {


        String hash= passwordEncoder.encode(signupRequest.getPassword());
        try {
            User addedUser = userService.registerUser(signupRequest, hash);
            if(signupRequest.getRole().equals("PROVIDER_ROLE")) {
                Lawyer addedLawyer = lawyerService.registerLawyer(signupRequest,addedUser);
            }
            otpService.clearOtp(signupRequest.getEmail());
            AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(
                    addedUser.getId(),
                    addedUser.getEmail(),
                    addedUser.getUsername(),
                    addedUser.getPhone(),
                    addedUser.getRole());
            AuthResponse authResponse = new AuthResponse(userDetails,true,"");
            return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
        }
        catch(UnauthorizedException ex)
        {
            AuthResponse authResponse = new AuthResponse(null,false,"Cannot get admin role illegally");
            return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.FORBIDDEN);
        }
        catch(NoSuchElementException ex)
        {
            AuthResponse authResponse = new AuthResponse(null, false, "Could not find the required role in database");
            return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpServletResponse response){
        try{
            return authenticate(email, password, request, response);
        }
        catch (NoSuchElementException ex)
        {
            AuthResponse authResponse = new AuthResponse(null , false,"Email doesn't exists");
            return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.FORBIDDEN);
        }
        catch (AuthenticationException ex)
        {
            AuthResponse authResponse = new AuthResponse(null,false,"Password doesn't match");
            return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.FORBIDDEN);
        }
    }

    @NotNull
    private ResponseEntity<AuthResponse> authenticate(String email, String password, HttpServletRequest request, HttpServletResponse response) {
        User fetchedUser = userService.getUserByEmail(email).orElseThrow();
        AuthenticatedUserDetails userDetails = new AuthenticatedUserDetails(
                fetchedUser.getId(),
                fetchedUser.getEmail(),
                fetchedUser.getUsername(),
                fetchedUser.getPhone(),
                fetchedUser.getRole());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly; Secure; SameSite=None");
        AuthResponse authResponse = new AuthResponse(userDetails,true, "");
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return new ResponseEntity<Boolean> (true,HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean> (false,HttpStatus.FORBIDDEN);
        }
    }


}

