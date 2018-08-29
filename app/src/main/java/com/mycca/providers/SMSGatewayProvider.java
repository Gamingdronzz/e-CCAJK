package com.mycca.providers;

import com.mycca.interfaces.IMessagingService;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.OTPManager;

import java.util.Locale;
import java.util.Random;

public class SMSGatewayProvider implements IMessagingService {

    @Override
    public String getApiBase() {
        return "https://platform.clickatell.com/messages/http/send";
    }

    @Override
    public String getApiKey() {
        return "apiKey";
    }

    @Override
    public String getApiValue() {
        return "A2hCUVGrTOieL2m3U2Tupw==";
    }

    @Override
    public String getReceiverKey() {
        return "to";
    }

    @Override
    public String getMessageKey() {
        return "content";
    }

    @Override
    public String getMessage() {
        return "OTP to continue submission in My CCA is " + getOTP() + ". Do not share with anyone";
    }

    @Override
    public String getOTP() {
        Random random = new Random();
        String generatedPassword = String.format(Locale.getDefault(), "%04d", random.nextInt(10000));
        CustomLogger.getInstance().logDebug("Generated Password : " + generatedPassword);
        OTPManager.generated=generatedPassword;
        return generatedPassword;
    }

    @Override
    public String getTag() {
        return "ClickaTell";
    }

}
