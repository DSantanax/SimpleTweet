package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Media;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUserAndImg;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

// TODO video play (possibly), and detail screen, also add retweet, fav, and reply functions
// TODO adjust SQL usage SELECTS, Hide float
public class TimelineActivity extends AppCompatActivity implements ComposeFragment.TweetComposeDialog {

    // references
    TwitterClient client;
    // hold tweets
    List<Tweet> tweets;
    RecyclerView rvTweets;
    TweetsAdapter tweetsAdapter;
    SwipeRefreshLayout swipeContainer;
    // abstract class, once we instantiate we can implement(override) the necessary methods
    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    // used to reference the DB Dao
    TweetDao tweetDao;
    // ref. to floating button
    FloatingActionButton btnFloat;

    private final int REQUEST_CODE = 99;

    public static final String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // show the view
        setContentView(R.layout.activity_timeline);

        // create a client to call requests
        client = TwitterApp.getRestClient(this);
        // Get reference to the database in the application context TweetDao
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();
        // swipe reference
        swipeContainer = findViewById(R.id.swipeContainer);
        // find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
        // remove SimpleTweet title
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // hide navigation (along with onWindow override function, removed hiding functionality
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

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
        // add divider between the recycle items
        rvTweets.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // init the list of tweets and adapter, pass the context, and list of tweets
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);
        // recycler view setup, layout manager & adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetsAdapter);
        btnFloat = findViewById(R.id.btnFloat);

        // show dialog
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeDialog();
            }
        });

        // instantiate the endless scroll listener, this takes in a layout manager
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            // load more data onto the rv
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "Loading more data for page: " + page);
                loadMoreTweets();
            }
        };
        // add the scroll listener to the recyclerview
        rvTweets.addOnScrollListener(endlessRecyclerViewScrollListener);

        // Query for existing tweets in the DB
        // Here we use Async to prevent throttling on the Main thread (parallel)
        // this can crash our app if we do it single threaded
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // call the query to get the tweets
                List<TweetWithUserAndImg> tweetWithUserAndImgs = tweetDao.getRecentTweetList();
                // once we get the Tweets with User & Media we want to break it into the objects
                List<Tweet> tweetsFromDB = TweetWithUserAndImg.getTweetList(tweetWithUserAndImgs);
                Log.i(TAG, "Loading/Getting data from Database: " + tweetsFromDB.toString());
                // clear the adapter list first
                tweetsAdapter.clear();
                // call the adapter to update
                tweetsAdapter.addAll(tweetsFromDB);

            }
        });

        // after setting up the RecyclerView, we need to populate the data
        populateHomeTimeline();
    }

    // method to request more tweets
    private void loadMoreTweets() {
                // call the get next page of tweets with Json param and id for max_id
                client.getNextPageOfTweets(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "On success loading more data for page");
                        // deserialize and construct new model objects from the API response
                        JSONArray jsonArray = json.jsonArray;
                        try {
                            // Get tweets from array as list
                            List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                            // do not need to call adapter clear() method since we are adding more
                            // 3. append the new data objects to the existing set of items inside
                            // the array of items
                            // 4. this adds the new tweets to the list and notifies the adapter
                            // within the adapter class itself
                            tweetsAdapter.addAll(newTweets);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure Failed to get more tweets!", throwable);
                    }
                    // get the last tweet id to get older tweets from that point on
                },  (tweets.get(tweets.size() - 1)).id);

    }

    // Menu icons are inflated (same as ActionBar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater and inflate the menu_main; this adds items to the action bar if present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // return true to the menu to be displayed
        return true;
    }

    // handle the ActionBar click for the menu item that was tapped
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if the item selected has the ID compose go to it (menu_main)
//        if (item.getItemId() == R.id.compose) {
            // compose icon has been selected
            // navigate to the compose icon
            // Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
            // --- now handled by FLOAT ---
              // intent was used before but switched to overlay modal
             // create intent going from this context to the ComposeActivity
             //   Intent intent = new Intent(this, ComposeActivity.class);
            // if we expect a result from the activity we can use startActivityForResult
            // this takes the intent and a CODE to distinguish the which activity
            // startActivityForResult(intent, REQUEST_CODE);

            // } else
            if (item.getItemId() == R.id.logOut) {
            // use client to clear token
            client.clearAccessToken();
            // go back to the login using an Intent
            Intent intent = new Intent(this, LoginActivity.class);
            // if we expect a result from the activity we can use startActivityForResult
            // this takes the intent and a CODE to distinguish the which activity
            startActivity(intent);
            // prevent user from going back to their screen (back button)
            finish();
        }
        // return true to consume the tap here
        return true;
    }

    // show fragment dialog
    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance();
        composeFragment.show(fm, "TweetCompose");
    }

    // used to handle data received from other activities, NO LONGER USED (Fragment Dialog is used instead)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // check if the Compose activity send the data using the request code
        // the RESULT_OK to check if the results were successfully sent using RESULT_OK activity built in
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get data from the Intent (tweet) by unwrapping the passed Parcel object
            Tweet tweet = (Tweet) Parcels.unwrap(Objects.requireNonNull(data).getParcelableExtra("tweet"));
            // update the RV with the Tweet
            // modify data source by adding the tweet to the front
            tweets.add(0, tweet);
            // update the adapter that we inserted an item as position 0
            tweetsAdapter.notifyItemInserted(0);
            // after we add the data smooth scroll to the new Tweet 0
            rvTweets.smoothScrollToPosition(0);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // this is used to perform the get request
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // get the data on behalf of the user, this will be stored in the json object if successful
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess" + json);
                try {
                    // to reference the jsonArray data which is received as an array of tweet objects
                    JSONArray jsonArray = json.jsonArray;
                    // set the tweets reference to the list of tweets we setup, using addAll to not
                    // create a new list, rather use the existing list
                    // call factory static method
//                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // notify the adapter the data changed from the model to the r.v.
//                    tweetsAdapter.notifyDataSetChanged();

                    // this those the above work, but for the swipe to refresh functionality
                    // clear the old data list to append the new ones
                    tweetsAdapter.clear();
                    // add new data, this needs to be declared final since it accessed in inner class
                    // an anonymous class
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    // load onto the screen and notify adapter all in one
                    tweetsAdapter.addAll(tweetsFromNetwork);
                    // signal refresh had finished, (to not show the loading indicator)
                    swipeContainer.setRefreshing(false);

                    // used to save the data
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving/Putting data into the Database from API call");
                            // first we insert users then media, order needs to be preserved to have a foreign key connection
                            // since Tweet needs to reference the Media and the User keys
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            List<Media> mediaFromNetwork = Media.fromJsonTweetArray(tweetsFromNetwork);
                            // save the query to get the tweets
                            // here we insert the ArrayList into an array with new Media[0] to resize itself
                            tweetDao.insertModel(mediaFromNetwork.toArray(new Media[0]));
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON data");
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                // output errors
                Log.e(TAG, "onFailure: " + response, throwable);
            }
            // get all new tweets using 1
        });
    }
    @Override
    public void onFinishTweetComposeDialog(Tweet tweet) {
        // add the tweet the data source list
        tweets.add(0, tweet);
        // notify the adapter an intem was inserted at position 0
        tweetsAdapter.notifyItemInserted(0);
        // smooth scroll to position in the RecyclerView
        rvTweets.smoothScrollToPosition(0);
        // Notify user tweet was posted
        Snackbar.make(rvTweets, "Tweet posted!", Snackbar.LENGTH_LONG).show();
    }

    // smooth scroll to top
    public void gotoTop(View view) {
        rvTweets.smoothScrollToPosition(0);
    }


    // handle data coming from the TweetComposeFragment
    // hide navigation bar, this caused issues with responsiveness of the FAB and transparency
    // removed this functionality
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            // immersive sticky causes transparency issues
//            getWindow().getDecorView().setSystemUiVisibility(
//
//                            // Set the content to appear under the system bars so that the
//                            // content doesn't resize when the system bars hide and show.
//                             View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            // Hide the nav bar and status bar
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            );
//        }    }

    // logging out method, log out is now handled by toolbar
    public void logOut(View view) {
        // use client to clear token
        client.clearAccessToken();
        // go back to the login using an Intent
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
//        prevent user from going back to their screen (back button)
        finish();
    }
}