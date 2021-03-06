package com.codepath.apps.restclienttemplate.models;

// Turn the JSON data into a Tweet object

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

// 2 annotations, RoomDAO & Parcel
// 2 foreign keys (one for Media, one for User)
@Entity(foreignKeys = { @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        @ForeignKey(entity = Media.class, parentColumns = "id", childColumns = "mediaId") })
@Parcel
public class Tweet {
    // fields we want from the request JSON method
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String body;
    // when the tweet was created
    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public String retweetCount;

    @ColumnInfo
    public String favCount;

    @ColumnInfo
    public long mediaId;

    // Here we ignore both the User and Media since we don't want a column
    // for these, instead we reference them with a foreign key
    // User within the tweet
    @Ignore
    public User user;
    // the ID to check tweet order (new/old)
    // Media
    @Ignore
    public Media media;

    // For parcel
    public Tweet(){ }

    // For inheritance - N/A we are using composition
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
        tweet.id = jsonObject.getLong("id");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = TimeFormatter.getTimeDifference(jsonObject.getString("created_at"));
        // create User object to define the userId reference and the user
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;
        tweet.retweetCount = jsonObject.getString("retweet_count");
        if(jsonObject.has("favorite_count")) {
            tweet.favCount = jsonObject.getString("favorite_count");
        }
        else{
            tweet.favCount = "0";
        }
        // check if we have a media object else keep it as null
        if(jsonObject.has("extended_entities")){
            // create media object
            Media media = Media.fromJson(jsonObject.getJSONObject("extended_entities"));
            // assign to tweet the media object and the reference id
            tweet.media = media;
            // assign same ID to reference
            tweet.mediaId = media.id;
        }
        else {
            // NO LONGER NULL
            // set media empty ID to 0 by default for DB
            Media mediaEmpty = new Media();
            mediaEmpty.id = 0;
            // assign to tweet the media object and the 0 id
            tweet.media = mediaEmpty;
            tweet.mediaId = mediaEmpty.id;
        }
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
