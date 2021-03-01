package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// Wrapper to do SQL on the SQLite
@Dao
public interface TweetDao {
    // two operations
    // 1. when we start up the app query to load/get
    // This queries 3 tables Tweet, Media, & User
    // INNER JOIN on Tweet and User using the foreign key
    // here we are using AS for alias since we prefixed the TweetWithUserAndImg
    // User.* select all users
    // INNER JOIN media or Full OUTER JOIN
    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, User.*, Media.imageEmb AS media_imageEmb, Media.id AS media_id " +
            "FROM Tweet INNER JOIN User ON User.id = Tweet.userId " +
            "INNER JOIN Media ON Media.id = Tweet.mediaId  " +
            "ORDER BY Tweet.createdAt " +
            "DESC LIMIT 25")
    List<TweetWithUserAndImg> getRecentTweetList();

    // We use this to insert Models into the DB
    // the ellipsis is used to take N number of arguments
    // 2. save when we call the API request
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);
    // We also need to insert the User and the Media since Tweet insert only inserts
    // its fields and references to it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Media... medias);

}
