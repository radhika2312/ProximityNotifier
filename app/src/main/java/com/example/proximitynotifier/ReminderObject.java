package com.example.proximitynotifier;

public class ReminderObject {
    private String title_rem,date_rem,time_rem,longitude_rem,latitude_rem,place_rem,details_rem;

    public String getDetails_rem() {
        return details_rem;
    }

    public void setDetails_rem(String details_rem) {
        this.details_rem = details_rem;
    }

    public ReminderObject(String title_rem, String date_rem, String time_rem, String longitude_rem, String latitude_rem, String place_rem, String details_rem) {
        this.title_rem = title_rem;
        this.date_rem = date_rem;
        this.time_rem = time_rem;
        this.longitude_rem = longitude_rem;
        this.latitude_rem = latitude_rem;
        this.place_rem = place_rem;
        this.details_rem = details_rem;
    }

    public String getTitle_rem() {
        return title_rem;
    }

    public void setTitle_rem(String title_rem) {
        this.title_rem = title_rem;
    }

    public String getDate_rem() {
        return date_rem;
    }

    public void setDate_rem(String date_rem) {
        this.date_rem = date_rem;
    }

    public String getTime_rem() {
        return time_rem;
    }

    public void setTime_rem(String time_rem) {
        this.time_rem = time_rem;
    }

    public String getLongitude_rem() {
        return longitude_rem;
    }

    public void setLongitude_rem(String longitude_rem) {
        this.longitude_rem = longitude_rem;
    }

    public String getLatitude_rem() {
        return latitude_rem;
    }

    public void setLatitude_rem(String latitude_rem) {
        this.latitude_rem = latitude_rem;
    }

    public String getPlace_rem() {
        return place_rem;
    }

    public void setPlace_rem(String place_rem) {
        this.place_rem = place_rem;
    }
}
