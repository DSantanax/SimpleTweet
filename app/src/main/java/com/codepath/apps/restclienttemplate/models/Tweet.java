package com.codepath.apps.restclienttemplate.models;

// Turn the JSON data into a Tweet object

import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    // fields we want from the request JSON method
    public String body;
    // when the tweet was created
    public String createdAt;
    // User within the tweet
    public User user;
    // the ID to check tweet order (new/old)
    public long id;

    // factory method to create tweets, caller handles error
    // the exceptions
    public static Object fromJson(JSONObject jsonObject) throws JSONException {
        // create tweet object
        Tweet tweet = new Tweet();
        // must be the exact JSON key name to map it into the value
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");

        return tweet;
    }

    // the JSON res will be an array of objects, we want to grab each object within the array
    // and create a Tweet from it along with the user
    public static List<Object> fromJsonArray(JSONArray jsonArray) throws JSONException {
        // list of Tweet objects
        List<Object> tweets = new ArrayList<>();
        // loop through the JSON array to get the individual tweet objects
        for(int i = 0 ; i < jsonArray.length(); i ++){
            // we call the Tweet fromJson method which accepts the Tweet objects
            if(jsonArray.getJSONObject(i).has("extended_entities")) {
                tweets.add(TweetImg.fromJson(jsonArray.getJSONObject(i)));
            }
            else {
                tweets.add(fromJson(jsonArray.getJSONObject(i)));
            }
        }
        return tweets;
    }
    public String getFormattedTimeStamp() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

}
