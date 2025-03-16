package com.KnowLaw.backend.Endpoints;

import com.KnowLaw.backend.Controller.AuthController;
import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.Role;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Exception.UnauthorizedException;
import com.KnowLaw.backend.Model.AuthenticatedUserDetails;
import com.KnowLaw.backend.Service.IEmailService;
import com.KnowLaw.backend.Service.IOtpService;
import com.KnowLaw.backend.Service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private IUserService userService;
    @Mock
    private IOtpService otpService;
    @Mock
    private IEmailService emailService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestSignup_NewUser() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("newuser");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());
        when(otpService.generateOtp()).thenReturn("123456");
        ResponseEntity<String> response = authController.RequestSignup(signupRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OTP sent to your email address. Please verify.", response.getBody());
        verify(otpService).generateOtp();
        verify(otpService).storeOtp(eq("new@example.com"), eq("123456"));
        verify(emailService).sendOtp(eq("new@example.com"), eq("123456"));
    }

    @Test
    void testRequestSignup_ExistingEmail() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("existing@example.com");

        when(userService.getUserByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authController.RequestSignup(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists, choose a different email", response.getBody());
    }

    @Test
    void testRequestSignup_ExistingUsername() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("existinguser");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername("existinguser")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authController.RequestSignup(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists, choose a different username", response.getBody());
    }

    @Test
    void testFinaliseSignup_Success() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("newuser");
        signupRequest.setPassword("password");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail("new@example.com");
        newUser.setUsername("newuser");
        newUser.setPhone("1234567890");
        newUser.setRole(new Role());

        when(userService.registerUser(any(SignupRequestDto.class), anyString())).thenReturn(newUser);

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new@example.com", response.getBody().getEmail());
        assertEquals("newuser", response.getBody().getUsername());
        verify(otpService).clearOtp("new@example.com");
    }

    @Test
    void testFinaliseSignup_InvalidOtp() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("newuser");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(false);

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testLogin_Success() {
        String email = "user@example.com";
        String password = "password";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setUsername("username");
        user.setPhone("1234567890");
        user.setRole(new Role());

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        ResponseEntity<AuthenticatedUserDetails> responseEntity = authController.login(email, password, request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(email, responseEntity.getBody().getEmail());
        assertNotNull(response.getHeader("Set-Cookie"));
    }

    @Test
    void testLogin_Failure() {
        String email = "user@example.com";
        String password = "wrongpassword";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(new User()));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        ResponseEntity<AuthenticatedUserDetails> responseEntity = authController.login(email, password, request, response);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        assertNull(response.getHeader("Set-Cookie"));
    }

    @Test
    void testIsAuthenticated_AuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");

        ResponseEntity<Boolean> response = authController.isAuthenticated(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User is authenticated as: user@example.com", response.getBody());
    }

    @Test
    void testIsAuthenticated_UnauthenticatedUser() {
        ResponseEntity<Boolean> response = authController.isAuthenticated(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User is not authenticated", response.getBody());
    }

    @Test
    void testFinaliseSignup_ExistingEmail() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("existing@example.com");
        signupRequest.setUsername("newuser");

        when(userService.getUserByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFinaliseSignup_ExistingUsername() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("existinguser");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername("existinguser")).thenReturn(Optional.of(new User()));

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFinaliseSignup_UnauthorizedException() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("newuser");
        signupRequest.setPassword("password");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.registerUser(any(SignupRequestDto.class), anyString())).thenThrow(new UnauthorizedException("test"));

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testFinaliseSignup_NoSuchElementException() {
        SignupRequestDto signupRequest = new SignupRequestDto();
        signupRequest.setEmail("new@example.com");
        signupRequest.setUsername("newuser");
        signupRequest.setPassword("password");

        when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userService.registerUser(any(SignupRequestDto.class), anyString())).thenThrow(new NoSuchElementException());

        ResponseEntity<AuthenticatedUserDetails> response = authController.Signup(signupRequest, "123456");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testLogin_UserNotFound() {
        String email = "nonexistent@example.com";
        String password = "password";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<AuthenticatedUserDetails> responseEntity = authController.login(email, password, request, response);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testLogin_AuthenticationException() {
        String email = "user@example.com";
        String password = "wrongpassword";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(new User()));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Authentication failed") {});

        ResponseEntity<AuthenticatedUserDetails> responseEntity = authController.login(email, password, request, response);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testIsAuthenticated_PartiallyAuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<Boolean> response = authController.isAuthenticated(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User is not authenticated", response.getBody());
    }
}
