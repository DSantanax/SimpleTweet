package com.codepath.apps.restclienttemplate.models;

// Turn the JSON data into a TweetImg object
import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

public class TweetImg extends Tweet {
    // for ImageView
    public String imageEmb;

    public TweetImg(String imageEmb, String body, String createdAt, User user, long id){
        super(body, createdAt, user, id);
        this.imageEmb = imageEmb;
    }



    // factory method to create tweets, caller handles error
    // the exceptions
    public static TweetImg fromJson(JSONObject jsonObject) throws JSONException {
        // must be the exact JSON key name to map it into the value
//        tweet.body = jsonObject.getString("text");
//        tweet.createdAt = jsonObject.getString("created_at");
//        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
//        tweet.id = jsonObject.getLong("id");
//        // get the image url (first check if there is any)
//        tweet.imageEmb = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url");


        TweetImg tweetImg = new TweetImg(jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url"),
                jsonObject.getString("text"),
                jsonObject.getString("created_at"),
                User.fromJson(jsonObject.getJSONObject("user")),
                jsonObject.getLong("id")
        );
        Log.i("TweetImg", tweetImg.imageEmb);
        return tweetImg;
    }

    public String getFormattedTimeStamp() {
        return super.getFormattedTimeStamp();
    }

}
