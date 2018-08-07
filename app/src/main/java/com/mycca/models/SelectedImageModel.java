package com.mycca.models;

import android.net.Uri;
import android.util.Log;

import java.io.File;

public class SelectedImageModel {

    private Uri imageURI;
    private File file;
    private String selectedImageName;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getSelectedImageName() {
        return selectedImageName;
    }

    public void setSelectedImageName(String selectedImageName) {
        this.selectedImageName = selectedImageName;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }

    public SelectedImageModel(Uri imageURI) {
        this.imageURI = imageURI;
        file = new File(imageURI.getPath());
        Log.d("IMage", "SelectedImageModel: "+file.length()/1024.0f);
        //this.selectedImageName = file.getName();
    }
}