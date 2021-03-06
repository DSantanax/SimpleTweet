package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

// Json data from the User
// Entity creates a table
@Parcel
@Entity
public class User {

    // fields we want from the request method'
    @PrimaryKey
    @ColumnInfo
    public long id;
    @ColumnInfo
    public String name;
    @ColumnInfo
    public String screenName;
    @ColumnInfo
    public String profileImageUrl;

    @ColumnInfo
    public String locationArea;

    // For parcel
    public User(){}

    // factory method to create Users, caller handles error
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.screenName = "@" + jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        if(jsonObject.has("location")) {
            user.locationArea = "- " + jsonObject.getString("location");
        } else {
            user.locationArea = "";
        }
        return user;
    }

    // to get the users from the network into a list to save for DB
    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> user = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            user.add(tweetsFromNetwork.get(i).user);
        }
        return user;
    }
}
