package com.ccajk.Tools;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ccajk.Interfaces.IConnectivityProcessor;
import com.ccajk.Listeners.OnConnectionAvailableListener;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by balpreet on 3/23/2018.
 */

public class ConnectionUtility implements IConnectivityProcessor {
    private OnConnectionAvailableListener onConnectionAvailableListener;


    final String TAG = "ConnectionUtility";

    public ConnectionUtility(OnConnectionAvailableListener onConnectionAvailableListener)
    {
        this.onConnectionAvailableListener = onConnectionAvailableListener;
    }

    @Override
    public void CheckConnectionAvailability() {
        String customURL = Helper.getInstance().getBaseURL();
        MyTask task = new MyTask();
        task.execute(customURL);
    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    HttpsURLConnection.setFollowRedirects(false);
                    HttpsURLConnection con = (HttpsURLConnection) new URL(params[0]).openConnection();
                    con.setRequestMethod("HEAD");
                    //System.out.println(con.getResponseCode());
                    return (con.getResponseCode() == HttpsURLConnection.HTTP_OK);
                }
                else
                {
                    return true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse==true)
            {
                Log.d(TAG,"URl Exists");
                if (onConnectionAvailableListener != null) {
                    onConnectionAvailableListener.OnConnectionAvailable();
                }
            }
            else
            {
                Log.d(TAG,"URl Does not Exist");
                if (onConnectionAvailableListener != null) {
                    onConnectionAvailableListener.OnConnectionNotAvailable();
                }
            }
        }
    }
}
