package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Media {
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String imageEmb;

    // Assign media from JSON
    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.imageEmb = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url");
        // get the ID to refer to the Tweet
        media.id = jsonObject.getJSONArray("media").getJSONObject(0).getLong("id");

        return media;
    }

    // save all the medias
    public static List<Media> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<Media> media = new ArrayList<>();
        // We need to add the exact amount to match the # of tweets, users, and medias
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            // add all medias to list (filled and empty)
            media.add(tweetsFromNetwork.get(i).media);
            // no longer checking for null
//            if(tweetsFromNetwork.get(i).media != null) {
//                media.add(tweetsFromNetwork.get(i).media);
//            }
//            else {
//                media.add(new Media());
//            }
        }
        return media;
    }
}
