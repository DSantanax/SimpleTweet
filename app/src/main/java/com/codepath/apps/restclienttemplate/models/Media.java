package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Media {
    public String imageEmb;

    // check if we need TODO
    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.imageEmb = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url");

        return media;
    }
}
