package com.mycca.Models;

import java.util.Date;

/**
 * Created by hp on 21-05-2018.
 */

public class NewsModel {

    Date date;
    String headline;
    String description;
    String state;

    public  NewsModel(){}

    public NewsModel(Date date, String headline, String description,String state) {
        this.date = date;
        this.headline = headline;
        this.description = description;
        this.state=state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
