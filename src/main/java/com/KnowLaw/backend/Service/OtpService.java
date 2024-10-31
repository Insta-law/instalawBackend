package com.KnowLaw.backend.Service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
public class OtpService implements IOtpService{

    final HashMap<String,String> otpStore = new HashMap<String,String>();

    @Override
    public String generateOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    public void storeOtp(String email, String otp){
        otpStore.put(email,otp);
    }

    @Override
    public boolean validateOtp(String email, @NotNull String otp){
        return otp.equals(otpStore.get(email));
    }

    @Override
    public void clearOtp(String email){
        otpStore.remove(email);
    }
}
