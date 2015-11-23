package com.company.defaultparser;

import java.util.Properties;

/**
 * Created by bbr on 26.09.15.
 */
public class Papers {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTitle() {
        try {
            if (title.contains("\'")) {
            }
            return title.replaceAll("'", "");
        } catch (Exception ex) {
            return ".i.";
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getEe() {
        return ee;
    }

    public void setEe(String ee) {
        this.ee = ee;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    private String mDate;
    private String date;
    private String title;
    private String ee;
    private String year;
    private String pages;
    private String venue;
    private String key;

}

/**
 * booktitle==venue for inproceedings
 * journal==venue for articles
 * series==venue for
 * school==venue for phd/master thesis
 */
