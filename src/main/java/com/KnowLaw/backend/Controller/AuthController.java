package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Lawyer;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Exception.UnauthorizedException;
import com.KnowLaw.backend.Model.AuthenticatedUserDetails;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Objects;

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
    public ResponseEntity<String> Signup(@RequestBody SignupRequestDto signupRequest , @RequestParam String otp){
        if(userService.getUserByEmail(signupRequest.getEmail()).isPresent())
            return new ResponseEntity<String>("Email already exists",HttpStatus.BAD_REQUEST);
        if(userService.getUserByUsername(signupRequest.getUsername()).isPresent())
            return new ResponseEntity<String>("Username already exists",HttpStatus.BAD_REQUEST);

        if(otpService.validateOtp(signupRequest.getEmail(), otp)) {
            ResponseEntity<AuthenticatedUserDetails> registerResponse = registerUser(signupRequest);
            if(registerResponse.getStatusCode() == HttpStatus.FORBIDDEN)
                return new ResponseEntity<String>("Cannot get admin role illegally",HttpStatus.FORBIDDEN);
            else if (registerResponse.getStatusCode() == HttpStatus.BAD_REQUEST)
                return new ResponseEntity<String>("Bad request sent",HttpStatus.BAD_REQUEST);
            else
                return new ResponseEntity<String>(Objects.requireNonNull(registerResponse.getBody()).toString(),HttpStatus.CREATED);

        }

        return new ResponseEntity<String>("Incorrect otp",HttpStatus.FORBIDDEN);
    }

    @NotNull
    @Transactional
    private ResponseEntity<AuthenticatedUserDetails> registerUser(SignupRequestDto signupRequest) {


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

            return new ResponseEntity<AuthenticatedUserDetails>(userDetails, HttpStatus.CREATED);
        }
        catch(UnauthorizedException ex)
        {
            return new ResponseEntity<AuthenticatedUserDetails>((AuthenticatedUserDetails) null,HttpStatus.FORBIDDEN);
        }
        catch(NoSuchElementException ex)
        {
            return new ResponseEntity<AuthenticatedUserDetails>((AuthenticatedUserDetails) null,HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserDetails> login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, HttpServletResponse response){
        try{
            return authenticate(email, password, request, response);
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            return new ResponseEntity<AuthenticatedUserDetails>((AuthenticatedUserDetails) null,HttpStatus.FORBIDDEN);
        }
    }

    @NotNull
    private ResponseEntity<AuthenticatedUserDetails> authenticate(String email, String password, HttpServletRequest request, HttpServletResponse response) {
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
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; HttpOnly; SameSite=Strict");
        return new ResponseEntity<AuthenticatedUserDetails>(userDetails, HttpStatus.OK);
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

