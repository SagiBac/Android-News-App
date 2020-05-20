package com.androidserverside.myapplication;

import android.graphics.Bitmap;

public class Restaurant {
    private String rating;
    private String name;
    private Bitmap image;
    private String UrlToResturant;
    private String latitude,longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Restaurant(){};

    public Restaurant(String rating, String name, Bitmap image, String urlToResturant) {
        this.rating = rating;
        this.name = name;
        this.image = image;
        UrlToResturant = urlToResturant;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getUrlToResturant() {
        return UrlToResturant;
    }

    public void setUrlToResturant(String urlToResturant) {
        UrlToResturant = urlToResturant;
    }
}
