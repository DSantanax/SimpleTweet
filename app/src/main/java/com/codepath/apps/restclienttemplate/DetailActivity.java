package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    ImageView ivDetailProImage;
    ImageView ivDetailClose;
    TextView tvDetailUsername;
    TextView tvDetailHandle;
    TextView tvDetailBody;
    TextView tvDetailTime;
    TextView tvLocation;
    ImageView ivDetailLikes;
    TextView tvDetailLikes;
    ImageView ivDetailRetweets;
    TextView tvDetailRetweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // reference the views
        ivDetailProImage = findViewById(R.id.ivDetailProImage);
        ivDetailClose = findViewById(R.id.ivDetailClose);
        tvDetailUsername = findViewById(R.id.tvDetailUsername);
        tvDetailHandle = findViewById(R.id.tvDetailHandle);
        tvDetailBody = findViewById(R.id.tvDetailBody);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvLocation = findViewById(R.id.tvLocation);

        ivDetailLikes = findViewById(R.id.ivDetailLikes);
        tvDetailLikes = findViewById(R.id.tvDetailLikes);
        ivDetailRetweets = findViewById(R.id.ivDetailRetweets);
        tvDetailRetweets = findViewById(R.id.tvDetailRetweets);


        // Unwrap tweet
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Log.i("Detail", "Hello " + tweet.user.screenName + tweet.body);
        // Image Views and Text Views
        tvDetailUsername.setText(tweet.user.name);
        tvDetailHandle.setText(tweet.user.screenName);
        tvDetailTime.setText(tweet.createdAt);
        tvLocation.setText(tweet.user.locationArea);
        tvDetailBody.setText(tweet.body);

        tvDetailLikes.setText(tweet.favCount);
        tvDetailRetweets.setText(tweet.retweetCount);

        // set image
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new RoundedCorners(10)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(ivDetailProImage);

        // close activity to return
        ivDetailClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });
    }
}