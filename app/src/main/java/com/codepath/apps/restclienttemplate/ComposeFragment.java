package com.codepath.apps.restclienttemplate;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.Objects;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {

    RelativeLayout rlFrag;
    EditText etCompose;
    TextInputLayout textCompose;
    Button btnTweet;
    // reference to client
    TwitterClient client;
    private static final int MAX_TWEET_LENGTH = 280;
    // by convention should be the activity name
    public static final String TAG = "ComposeActivity";


    // 1. Defines the listener interface with a method passing back data result.
    public interface TweetComposeDialog {
        void onFinishTweetComposeDialog(Tweet tweet);
    }

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment tweet_compose.
     */

    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the layout for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate view
        return inflater.inflate(R.layout.fragment_tweet_compose, container, false);
    }

    // when the Fragment is created get the references to the Views & add callbacks
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // get the current rest client (do not create a new one)
        client = TwitterApp.getRestClient(getContext());
        rlFrag = view.findViewById(R.id.rlFrag);
        // get the references to the ID's in the layout for activity compose
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);
        textCompose = view.findViewById(R.id.textCompose);
        // set call back for on click
        // button onClickListener
        btnTweet.setOnClickListener(this);
        // set callback for textChanged listener
        etCompose.addTextChangedListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    // Check if input text is 280 to disable button
    // These are overridden for the call backs
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnTweet.setEnabled(s.length() <= MAX_TWEET_LENGTH);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* leave to default */ }
    @Override
    public void afterTextChanged(Editable s) {/* leave to default */}
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {return false;}


    // on Click call back
    @Override
    public void onClick(View v) {
        // make an API call to twitter to publish the tweet
        final String tweetContent = etCompose.getText().toString();
        // check error cases if empty or too long
        // if it is return
        if (tweetContent.isEmpty()) {
            Snackbar.make(rlFrag, R.string.empty_status, Snackbar.LENGTH_SHORT)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // dismissed by user
                        }
                    }).show();
            return;
        }
        // if tweet exceeds capacity
        if (tweetContent.length() > MAX_TWEET_LENGTH) {
            Snackbar.make(rlFrag, R.string.empty_status, Snackbar.LENGTH_SHORT)
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

                Snackbar.make(rlFrag, R.string.posted_status, Snackbar.LENGTH_SHORT)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // dismissed by user
                            }
                        }).show();

                Log.i(TAG, "onSuccess to publish tweet");
                // Json object is a tweet model
                try {
                    // create tweet object
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    // Return input text back to activity through the implemented listener
                    TweetComposeDialog listener = (TweetComposeDialog) getActivity();
                    // pass the tweet object back
                    Objects.requireNonNull(listener).onFinishTweetComposeDialog(tweet);
                    // close the dialog and return to parent activity
                    dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Snackbar.make(rlFrag, R.string.dup_post, Snackbar.LENGTH_SHORT);
                Log.e(TAG, "onFailure to publish tweet: " + tweetContent, throwable);
            }
        });
    }
    // Resize the dialog fragment to match parent
    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = Objects.requireNonNull(window).getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width & height of the dialog proportional to 80% of the screen width
        // WindowManager.LayoutParams.WRAP_CONTENT returns the default value
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}
