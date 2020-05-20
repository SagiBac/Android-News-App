package com.androidserverside.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONLocationDetails {
    private String m_Name;
    private String m_Rate;
    private String m_Lat;
    private String m_Lng;

    public JSONLocationDetails(JSONObject rootObject) throws JSONException {
        JSONArray results = rootObject.getJSONArray("results");
        JSONObject currentObject = results.getJSONObject(0);
        m_Name = currentObject.getString("name");
        m_Rate = currentObject.getString("rating");
        JSONObject currentGeoObject = currentObject.getJSONObject("geometry").getJSONObject("location");
        m_Lat = currentGeoObject.getString("lat");
        m_Lng = currentGeoObject.getString("lng");
        JSONObject ImageDetailsJSON = currentObject.getJSONArray("photos").getJSONObject(0);
        final int ImageHeight = ImageDetailsJSON.getInt("height");
        final int ImageWidth = ImageDetailsJSON.getInt("width");
        final String ImageReference = ImageDetailsJSON.getString("photo_reference");
    }

    public String getM_Name() {
        return m_Name;
    }

    public void setM_Name(String m_Name) {
        this.m_Name = m_Name;
    }

    public String getM_Rate() {
        return m_Rate;
    }

    public void setM_Rate(String m_Rate) {
        this.m_Rate = m_Rate;
    }

    public String getM_Lat() { return m_Lat; }

    public void setM_Lat(String m_Lat) { this.m_Lat = m_Lat; }

    public String getM_Lng() { return m_Lng; }

    public void setM_Lng(String m_Lng) { this.m_Lng = m_Lng; }
}
