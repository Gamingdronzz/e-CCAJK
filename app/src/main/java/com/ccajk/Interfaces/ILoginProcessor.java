package com.ccajk.Interfaces;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by balpreet on 2/27/2018.
 */
public interface ILoginProcessor {

    void RequestLogin(String PensionerCode,String password);
    void OnLoginSuccesful(DataSnapshot dataSnapshot);
    void OnLoginFailure();
    void OnUserNotExist();

}
