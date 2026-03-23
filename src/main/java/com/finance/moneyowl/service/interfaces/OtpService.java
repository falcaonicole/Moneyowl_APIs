package com.finance.moneyowl.service.interfaces;

import com.finance.moneyowl.generatedmodels.OtpRequest;

public interface OtpService {
    public String sendOtp(String Id);

    public boolean verifyOtp(OtpRequest otpRequest);
}
