package com.codepath.apps.restclienttemplate.models;

// Turn the JSON data into a TweetImg object
import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;

public class TweetImg {
    // fields we want from the request JSON method
    public String body;
    // when the tweet was created
    public String createdAt;
    // User within the tweet
    public User user;
    // the ID to check tweet order (new/old)
    public long id;
    // initially empty
    public String imageEmb = "";

    // factory method to create tweets, caller handles error
    // the exceptions
    public static TweetImg fromJson(JSONObject jsonObject) throws JSONException {
        TweetImg tweet = new TweetImg();
        // must be the exact JSON key name to map it into the value
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        // get the image url (first check if there is any)
        if(jsonObject.has("extended_entities")) {
            tweet.imageEmb = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            Log.i("Tweet", tweet.user.name + " " + tweet.user.screenName + " " + tweet.imageEmb);
        }
        return tweet;
    }

    public String getFormattedTimeStamp() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

}
