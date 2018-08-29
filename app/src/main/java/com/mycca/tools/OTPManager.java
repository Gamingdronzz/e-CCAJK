package com.mycca.tools;

import android.app.Activity;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.VolleyError;
import com.mycca.R;
import com.mycca.interfaces.IMessagingService;
import com.mycca.listeners.OTPProcessCompleteListener;
import com.mycca.providers.SMSGatewayProvider;

import org.json.JSONObject;

import java.util.HashMap;

public class OTPManager implements VolleyHelper.VolleyResponse {

    public static String generated;
    private Activity context;
    private String mobileNumber;
    private OTPProcessCompleteListener otpProcessCompleteListener;

    public OTPManager(Activity context, String mobileNumber, OTPProcessCompleteListener otpProcessCompleteListener) {
        this.context = context;
        this.mobileNumber = mobileNumber;
        this.otpProcessCompleteListener = otpProcessCompleteListener;
    }


    public void sendSMS() {

        IMessagingService iMessagingService = new SMSGatewayProvider();

        HashMap<String, String> params = new HashMap<>();
        params.put(iMessagingService.getApiKey(), iMessagingService.getApiValue());
        params.put(iMessagingService.getReceiverKey(), mobileNumber);
        params.put(iMessagingService.getMessageKey(), iMessagingService.getMessage());

        VolleyHelper volleyHelper = new VolleyHelper(this, context);
        volleyHelper.makeStringRequest(iMessagingService.getApiBase(), iMessagingService.getTag(), params);
        showOTPDialog();
    }

    private void showOTPDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_enter_otp, null);

        final TextInputEditText input = viewInflated.findViewById(R.id.otp);

        builder.setView(viewInflated);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String otp = input.getText().toString();
            checkOTPCorrect(otp);
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> { });
        builder.show();
    }

    private void checkOTPCorrect(String otp) {
        if (generated.equals(otp))
            otpProcessCompleteListener.onOTPMatchSuccess();
        else {
            Helper.getInstance().showErrorDialog(context.getString(R.string.enter_correct),
                    context.getString(R.string.wrong_otp), context);
        }
    }

    @Override
    public void onError(VolleyError volleyError) {
       volleyError.getStackTrace();
    }

    @Override
    public void onResponse(String str) {
        JSONObject jsonObject = Helper.getInstance().getJson(str);
        CustomLogger.getInstance().logDebug(jsonObject.toString());
    }
}
