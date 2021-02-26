package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

// Tweet adapter
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    // references
    Context context;
    // Data usage
    List<Tweet> tweets;

    // pass in the context & list of tweets in the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    // foreach row, inflate a layout for the tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data at position
        Tweet tweet = tweets.get(position);
        // bind the tweet at the ViewHolder
        holder.bind(tweet);
    }

    // get the item count
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // define inner class view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;

        // itemView is a representation of one row in the recycle view, a tweet
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);

        }
        // method in ViewHolder to bind the data to the views
        public void bind(Tweet tweet) {
            // using the views we bind the data to it
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            // using glide we load the image into the ImageView
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
        }
    }

    // methods for the Swipe to refresh layout functionality

    // clear elements of the recyclerview
    public void clear() {
        // clear the data from the list, want to modify the reference ONLY
        // no new array list, modify existing
        tweets.clear();
        // notify the adapter of the change
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<Tweet> newTweets) {
        tweets.addAll(newTweets);
        notifyDataSetChanged();
    }

}
