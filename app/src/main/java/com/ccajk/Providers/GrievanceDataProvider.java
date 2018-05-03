package com.ccajk.Providers;

import android.util.Log;

import com.ccajk.Models.GrievanceModel;

import java.util.ArrayList;

/**
 * Created by hp on 28-04-2018.
 */

public class GrievanceDataProvider {

    private static GrievanceDataProvider _instance;

    private ArrayList<GrievanceModel> allGrievanceList;
    private ArrayList<GrievanceModel> submittedGrievanceList;
    private ArrayList<GrievanceModel> processingGrievanceList;
    private ArrayList<GrievanceModel> resolvedGrievanceList;

    public GrievanceModel selectedGrievance;
    //public int selectedGrievancePosition;

    public GrievanceDataProvider() {
        _instance = this;
    }

    public static GrievanceDataProvider getInstance() {
        if (_instance == null) {
            return new GrievanceDataProvider();
        } else {
            return _instance;
        }
    }

    public ArrayList<GrievanceModel> getAllGrievanceList() {
        return allGrievanceList;
    }

    public void setAllGrievanceList(ArrayList<GrievanceModel> allGrievanceList) {
        this.allGrievanceList=new ArrayList<>();
        this.allGrievanceList = allGrievanceList;
    }

    public ArrayList<GrievanceModel> getSubmittedGrievanceList() {
        return submittedGrievanceList;
    }

    public void setSubmittedGrievanceList(ArrayList<GrievanceModel> submittedGrievanceList) {
        this.submittedGrievanceList = submittedGrievanceList;
    }

    public ArrayList<GrievanceModel> getProcessingGrievanceList() {
        return processingGrievanceList;
    }

    public void setProcessingGrievanceList(ArrayList<GrievanceModel> processingGrievanceList) {
        this.processingGrievanceList = processingGrievanceList;
    }

    public ArrayList<GrievanceModel> getResolvedGrievanceList() {
        return resolvedGrievanceList;
    }

    public void setResolvedGrievanceList(ArrayList<GrievanceModel> resolvedGrievanceList) {
        this.resolvedGrievanceList = resolvedGrievanceList;
    }

    public void updateLists() {
        submittedGrievanceList.remove(selectedGrievance);
        processingGrievanceList.remove(selectedGrievance);
        resolvedGrievanceList.remove(selectedGrievance);

        if (selectedGrievance.getGrievanceStatus() == 0) {
            submittedGrievanceList.add(selectedGrievance);
//            processingGrievanceList.remove(selectedGrievance);
//            resolvedGrievanceList.remove(selectedGrievance);
        } else if (selectedGrievance.getGrievanceStatus() == 1) {
            processingGrievanceList.add(selectedGrievance);
//            submittedGrievanceList.remove(selectedGrievance);
//            resolvedGrievanceList.remove(selectedGrievance);
        } else {
            resolvedGrievanceList.add(selectedGrievance);
//            processingGrievanceList.remove(selectedGrievance);
//            submittedGrievanceList.remove(selectedGrievance);
        }
        Log.v("Data Provider ", "submitted: "+ submittedGrievanceList.size());
        Log.v("Data Provider ", "processing: "+ processingGrievanceList.size());
        Log.v("Data Provider ", "resolved: "+ resolvedGrievanceList.size());
    }

}
