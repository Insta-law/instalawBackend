package com.KnowLaw.backend.Service;

import com.KnowLaw.backend.Dto.SignupRequestDto;
import com.KnowLaw.backend.Entity.User;
import com.KnowLaw.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*@Override
    @Transactional
    public User signupOrLoginUser(String googleId, String name, String email) {

        try {
            // Check if the user already exists

            User user = userRepository.findByGoogleId(googleId)
                    .orElseGet(() -> {
                        UserDto newUser = new UserDto();
                        newUser.setGoogleId(googleId);
                        newUser.setName(name);
                        newUser.setEmail(email);
                        return UserMapper.toEntity(newUser);
                    });

            // Save or update the user in the database
            return userRepository.save(user);
        }
        catch(Exception e){
            throw new RuntimeException("User login/signup failed",e);
        }

    }

    @Override
    public UserDto getUserInfo(String googleId)
    {
        return UserMapper.toDto(userRepository.findByGoogleId(googleId).orElseThrow());
    }*/


    @Override
    @Transactional
    public void registerUser(SignupRequestDto signupRequest){
        String hash= passwordEncoder.encode(signupRequest.getPassword());
        User user= new User(signupRequest.getEmail(), signupRequest.getUsername(), signupRequest.getPhone(),hash);
        userRepository.save(user);
    }


}
