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
    // Media
    public Media media;

    // For fromJson call
    public Tweet(){ }

    // For inheritance
    public Tweet(String body, String createdAt, User user, long id) {
        this.body = body;
        this.createdAt = createdAt;
        this.user = user;
        this.id = id;
    }

    // factory method to create tweets, caller handles error
    // the exceptions
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        // create tweet object
        Tweet tweet = new Tweet();
        // must be the exact JSON key name to map it into the value
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");

        // check if we have a media object else keep it as null
        if(jsonObject.has("extended_entities")){
            tweet.media = Media.fromJson(jsonObject.getJSONObject("extended_entities"));
        }
        // else media is just null


        return tweet;
    }

    // the JSON res will be an array of objects, we want to grab each object within the array
    // and create a Tweet from it along with the user
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        // list of Tweet objects
        List<Tweet> tweets = new ArrayList<>();
        // loop through the JSON array to get the individual tweet objects
        for(int i = 0 ; i < jsonArray.length(); i ++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
    public String getFormattedTimeStamp() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

}
