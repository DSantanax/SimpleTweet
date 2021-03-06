package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	SampleModelDao sampleModelDao;
	ImageView ivTweet;
	RotateAnimation rotateAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// This is a sample model on how DAO Room is used
		final SampleModel sampleModel = new SampleModel();
		sampleModel.setName("CodePath");
		// get Image view
		ivTweet = findViewById(R.id.ivTweet);

		// set tweet animation
		rotateAnimation = new RotateAnimation(30, 90 , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ivTweet.setAnimation(rotateAnimation);

		// Get reference to the database in the application context
		sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();

		// Here we use Async to prevent throttling on the Main thread (parallel)
		// this can crash our app if we do it single threaded
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				sampleModelDao.insertModel(sampleModel);
			}
		});
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		Log.i("Signed", "Login sucess");
		// go to TimeLineActivity after logging in
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
		 // prevent user from going back to the login (this activity) after login
		 finish();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

	// Animate image
    public void bumpImage(View view) {
		ivTweet.startAnimation(rotateAnimation);
    }
}
