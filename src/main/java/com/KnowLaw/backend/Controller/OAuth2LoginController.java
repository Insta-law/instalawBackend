package com.KnowLaw.backend.Controller;

import com.KnowLaw.backend.Dto.UserDto;
import com.KnowLaw.backend.Service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.NoSuchElementException;


@RestController
public class OAuth2LoginController {

    @Value("${redirect.uri.success}")
    String redirectUriSuccess;
    @Value("${redirect.uri.error}")
    String redirectUriError;

    final String allowedOrigin="http://localhost:4200";
    @Autowired
    IUserService userService;

    @GetMapping("/auth/google/callback")
    public void loginOrSignup(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response) {
        if (principal == null) {
            try {
                response.sendRedirect(redirectUriError);
                return;
            } catch (IOException e) {
                throw new RuntimeException("Error while redirecting..", e);
            }
        }

        String googleId = principal.getAttribute("sub");
        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");

        try {
            userService.signupOrLoginUser(googleId, name, email);
        } catch (RuntimeException e) {
            throw new RuntimeException("User registration/login failed: " + e.getMessage());
        }

        // Redirect to Angular frontend after successful login/signup
        try {
            response.sendRedirect(redirectUriSuccess+"&user="+googleId);
        } catch (IOException e) {
            throw new RuntimeException("Error while redirecting..", e);
        }
    }

    @CrossOrigin(origins = allowedOrigin)
    @GetMapping("/userInfo")
    public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal OAuth2User principal)
    {
        if(principal == null)
        {
            return new ResponseEntity<> (HttpStatus.UNAUTHORIZED);
        }

        try{
            return new ResponseEntity<UserDto>(userService.getUserInfo(principal.getAttribute("sub")),HttpStatus.OK);
        }
        catch(NoSuchElementException ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
