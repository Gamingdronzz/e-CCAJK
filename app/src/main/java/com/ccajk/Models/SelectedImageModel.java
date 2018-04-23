package com.ccajk.Models;

import android.net.Uri;

import java.io.File;

/**
 * Created by balpreet on 4/24/2018.
 */

public class SelectedImageModel {

    private Uri imageURI;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    private File file;
    private String selectedImageName;

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
        //this.selectedImageName = file.getName();

    }




}