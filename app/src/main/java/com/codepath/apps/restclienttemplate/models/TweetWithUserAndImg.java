package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

// This will handle the result generated by the TweetDao
// Which is the combination of Tweet, User, & Media
public class TweetWithUserAndImg {

    // Embedded notation flattens the properties of the User object into the object, preserving encapsulation
    @Embedded
    User user;
    // prefix since user and tweet have an id w/ same name
    @Embedded(prefix = "tweet_")
    Tweet tweet;
    // prefix since user and media have an id w/ same name
    @Embedded(prefix = "media_")
    Media media;

    // Here we break down the query results into tweet, user, and media
    public static List<Tweet> getTweetList(List<TweetWithUserAndImg> tweetWithUserAndImgs) {
        // list to return the tweets
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < tweetWithUserAndImgs.size(); i++) {
            // assign the tweet, user, and media (previous mistake)!
            Tweet tweet = tweetWithUserAndImgs.get(i).tweet;
            tweet.user = tweetWithUserAndImgs.get(i).user;
            // If it has a media object add it to the tweet, TODO remove parenthesis
//             if(tweetWithUserAndImgs.get(i).media.id != 0) {
            // get all medias (even the empty constructor ones)
                tweet.media = tweetWithUserAndImgs.get(i).media;
//                }
            tweets.add(tweet);
        }
        return tweets;
    }
}
