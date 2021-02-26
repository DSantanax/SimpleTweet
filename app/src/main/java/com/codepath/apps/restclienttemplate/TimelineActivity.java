package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    // references
    TwitterClient client;
    // hold tweets
    List<Tweet> tweets;
    RecyclerView rvTweets;
    TweetsAdapter tweetsAdapter;
    SwipeRefreshLayout swipeContainer;

    public static final String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show the view
        setContentView(R.layout.activity_timeline);

        // create a client to call requests
        client = TwitterApp.getRestClient(this);

        // swipe reference
        swipeContainer = findViewById(R.id.swipeContainer);

        // configure the refreshing colors for the loading, default is the solid black
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
               android.R.color.holo_orange_light, android.R.color.holo_red_dark);

        // add the on refresh listener with the callback
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data");
                // then add the new data
                populateHomeTimeline();
            }
        });

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // init the list of tweets and adapter, pass the context, and list of tweets
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);
        // recycler view setup, layout manager & adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetsAdapter);

        // after setting up the RecyclerView, we need to populate the data
        populateHomeTimeline();
    }

    // this is used to perform the get request
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // get the data on behalf of the user, this will be stored in the json object if successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");
                try {
                    // to reference the jsonArray data which is received as an array of tweet objects
                    JSONArray jsonArray = json.jsonArray;
                    // set the tweets reference to the list of tweets we setup, using addAll to not
                    // create a new list, rather use the existing list
                    // call factory static method
//                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // notify the adapter the data changed from the model to the r.v.
//                    tweetsAdapter.notifyDataSetChanged();

                    // this those the above work, for the swipe to refresh functionality

                    // clear the old data list to append the new ones
                    tweetsAdapter.clear();
                    // add new data
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // signal refresh had finished, (to not show the loading indicator
                    swipeContainer.setRefreshing(false);

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON data");
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                // output errors
                Log.e(TAG, "onFailure: " + response, throwable);
            }
        });
    }
}