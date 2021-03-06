package com.codepath.apps.restclienttemplate;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.User;

// This creates a Database w/ entities Tweet, User, Media

// When we create a new DB we can have issues with refactoring b/cos we have auto generated schema files
// we also must change the version to a different one every time we change the DB ^ based on this issue
@Database(entities={SampleModel.class, Tweet.class, User.class, Media.class}, version=18)
public abstract class MyDatabase extends RoomDatabase {
    // Sample Reference
    public abstract SampleModelDao sampleModelDao();
    // We reference the Dao for queries
    public abstract TweetDao tweetDao();
    // Database name to be used
    public static final String NAME = "MyDataBase";
}
