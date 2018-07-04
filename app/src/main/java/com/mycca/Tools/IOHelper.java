package com.mycca.Tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mycca.Listeners.ReadFileCompletionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOHelper {

    private static IOHelper _instance;
    private String TAG = "iohelper";


    private IOHelper() {
        _instance = this;
    }

    public static IOHelper getInstance() {
        if (_instance == null) {
            return new IOHelper();
        } else {
            return _instance;
        }
    }

    public void writeToFile(Object jsonObject, String filename, Context context) {
        new WriteFile().execute(context, filename, jsonObject);
    }

    public void readFromFile(String filename, Context context, ReadFileCompletionListener readFileCompletionListener) {
        new ReadFile().execute(context, filename, readFileCompletionListener);
    }

    class WriteFile extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
            Context context = (Context) objects[0];
            String filename = (String) objects[1];
            String jsonObject = (String) objects[2];

            String folder = Preferences.getInstance().getStringPref(context, Preferences.PREF_STATE);
            File path = new File(context.getFilesDir(), folder);
            path.mkdirs();
            File file = new File(path, filename + ".json");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(jsonObject.getBytes());
                Log.d(TAG, "WRITING TO FILE: " + file.getCanonicalPath());
                outputStream.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Could not write");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class ReadFile extends AsyncTask<Object, Object, Object> {

        ReadFileCompletionListener readFileCompletionListener;

        @Override
        protected Object doInBackground(Object... objects) {

            Context context = (Context) objects[0];
            String filename = (String) objects[1];
            readFileCompletionListener = (ReadFileCompletionListener) objects[2];

            String folder = Preferences.getInstance().getStringPref(context, Preferences.PREF_STATE);
            File path = new File(context.getFilesDir(), folder);
            path.mkdirs();
            File file = new File(path, filename + ".json");
            try {
                Log.d(TAG, "readFromFile: file path = " + file.getPath());
                FileInputStream fin = new FileInputStream(file);
                int size = fin.available();
                byte[] buffer = new byte[size];
                fin.read(buffer);
                fin.close();
                return new String(buffer);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "could not read");
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            readFileCompletionListener.onFileRead(o);
        }
    }
}
