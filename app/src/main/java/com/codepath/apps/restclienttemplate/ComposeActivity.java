package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

//Compose tweet
public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;

    // TODO modify the edit text (look and feel)
    RelativeLayout rlCompose;
    EditText etCompose;
    TextInputLayout textCompose;
    Button btnTweet;
    // reference to client
    TwitterClient client;
    // by convention should be the activity name
    public static final String TAG = "ComposeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // get the current rest client (do not create a new one)
        client = TwitterApp.getRestClient(this);
        rlCompose = findViewById(R.id.rlCompose);
        // get the references to the ID's in the layout for activity compose
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        textCompose = findViewById(R.id.textCompose);
        // button onClickListener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // make an API call to twitter to publish the tweet

                // TODO for error handling Toasts we can use android SnackBar instead
                final String tweetContent = etCompose.getText().toString();
                // check error cases if empty or too long
                // if it is return
                if (tweetContent.isEmpty()) {
                    Snackbar.make(rlCompose, R.string.empty_status, Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // dismissed by user
                                }
                            }).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Snackbar.make(rlCompose, R.string.empty_status, Snackbar.LENGTH_SHORT)
                            .setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // dismissed by user
                                }
                            }).show();
                    return;
                }
                // JsonHttpResponse Handler is used to handle the response
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        Snackbar.make(rlCompose, R.string.posted_status, Snackbar.LENGTH_SHORT)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // dismissed by user
                                    }
                                }).show();

                    Toast.makeText(ComposeActivity.this, "Posted status!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onSuccess to publish tweet");
                        // Json object is a tweet model
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            // create intent to pass data back to parent
                            Intent intent = new Intent();
                            // wrap object as Parcel to pass objects
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // send a result OK back to the parent (Result Code)
                            // and bundle the data for response (intent) return the data
                            setResult(RESULT_OK, intent);
                            // close the activity & pass the data to the parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Snackbar.make(rlCompose, R.string.dup_post, Snackbar.LENGTH_SHORT);
                        Log.e(TAG, "onFailure to publish tweet: " + tweetContent, throwable);
                    }
                });
            }
        });
        // action listener on the et field, if edit text > 140, disable the button
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* leave to default */ }
            // check when the text size changes to disable the button
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnTweet.setEnabled(s.length() <= MAX_TWEET_LENGTH);
            }
            @Override
            public void afterTextChanged(Editable s) { /* leave to default */ }
        });
    }
}