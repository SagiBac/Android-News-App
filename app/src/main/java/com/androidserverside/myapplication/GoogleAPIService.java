package com.androidserverside.myapplication;

public class GoogleAPIService {
    private final String GOOGLE_API_KEY = "AIzaSyASuG-qV-aYoZzte-n4iXnNy8iv5SIeXYQ";
    private final String API_ROOT = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private final String GOOGLE_PLACE_GETIMAGE = "https://maps.googleapis.com/maps/api/place/photo";
    private String FullApiURL;

    public GoogleAPIService()
    {}

    public String getFullApiURL() {
        return FullApiURL;
    }

    public void setFullApiURL(String fullApiURL) {
        FullApiURL = fullApiURL;
    }

    public String getGOOGLE_PLACE_GETIMAGE() {
        return GOOGLE_PLACE_GETIMAGE;
    }

    public String getAPI_ROOT() {
        return API_ROOT;
    }

    public String getGOOGLE_API_KEY() {
        return GOOGLE_API_KEY;
    }
}
