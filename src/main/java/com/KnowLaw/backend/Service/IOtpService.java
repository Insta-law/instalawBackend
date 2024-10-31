package com.KnowLaw.backend.Service;

public interface IOtpService {
    String generateOtp();

    void storeOtp(String email, String otp);

    boolean validateOtp(String email, String otp);

    void clearOtp(String email);
}
