package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

// Tweet adapter
public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // references
    Context context;
    // Data usage
    List<Tweet> tweets;

    private final int TEXT = 0, IMAGE = 1;

    // pass in the context & list of tweets in the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @Override
    public int getItemViewType(int position) {
        // check subclass class first. then child class
        // (NO LONGER USING NULL) if the media is not null or
        // (FROM DB) if the ID is not 0 (empty constructor) load the IMAGE View
        if(tweets.get(position).media.id != 0) {
            return IMAGE;
        }
        // else it is of type Text and has ID 0
        else {
            return TEXT;
        }
    }

    // foreach row, inflate a layout for the tweet
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case TEXT:
                View viewText = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new ViewHolderText(viewText);
                break;
            case IMAGE:
                View viewImg = inflater.inflate(R.layout.item_tweet_img, parent, false);
                viewHolder = new ViewHolderImg(viewImg);
                break;
            default:
                throw new IllegalStateException("Unexpected layout: " + viewType);
        }
//        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
//        return new ViewHolder(view);
        return viewHolder;
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        // get data at position
//        Tweet tweet = tweets.get(position);
//        // bind the tweet at the ViewHolder
//        holder.bind(tweet);
        switch (holder.getItemViewType()) {
            case TEXT:
                ViewHolderText vh1 = (ViewHolderText) holder;
                bind(vh1, position);
                break;
            case IMAGE:
                ViewHolderImg vh2 = (ViewHolderImg) holder;
                bindImg(vh2, position);
                break;
            default:
                throw new IllegalStateException("Unexpected layout: " + holder);
        }
    }
    // method in ViewHolder to bind the data to the views
    public void bind(ViewHolderText vh1, int position) {
        // using the views we bind the data to it
        Tweet tweet = tweets.get(position);
        vh1.tvBody.setText(tweet.body);
        vh1.tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        vh1.userName.setText(tweet.user.name);
        // use TimeFormatter to get the difference then format the time
        vh1.createdAt.setText(tweet.getFormattedTimeStamp());
        // using glide we load the image into the ImageView
        // using rounded corners we want to override the image to keep it the original size for consistency
        Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(10)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(vh1.ivProfileImage);
    }
    // method in ViewHolder to bind the data to the views
    public void bindImg(ViewHolderImg vh2, int position) {
        // using the views we bind the data to it
        Tweet tweet = tweets.get(position);
        vh2.tvBody.setText(tweet.body);
        vh2.tvScreenName.setText(String.format("@%s", tweet.user.screenName));
        vh2.userName.setText(tweet.user.name);
        // use TimeFormatter to get the difference then format the time
        vh2.createdAt.setText(tweet.getFormattedTimeStamp());
        // using glide we load the image into the ImageView
        // using rounded corners we want to override the image to keep it the original size for consistency
        Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(10)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(vh2.ivProfileImage);

        // Load body text image into glide
        Log.i("TweetsAdapter", "Loading image embedded " + tweet.media.imageEmb);
        Glide.with(context).load(tweet.media.imageEmb).transform(new RoundedCorners(25)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(vh2.ivMediaImage);
    }

    // get the item count
    @Override
    public int getItemCount() {
        return tweets.size();
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
// define class view holder
class ViewHolderText extends RecyclerView.ViewHolder {
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView userName;
    TextView createdAt;
    ImageView ivMediaImage;

    // itemView is a representation of one row in the recycle view, a tweet
    public ViewHolderText(@NonNull View itemView) {
        super(itemView);
        Log.i("Tweets Adapter", "Created ViewHolderText");
        ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        tvBody = itemView.findViewById(R.id.tvBody);
        tvScreenName = itemView.findViewById(R.id.tvScreenName);
        userName = itemView.findViewById(R.id.tvName);
        createdAt = itemView.findViewById(R.id.tvCreatedAt);
        ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
    }
}

// define class view holder 2
class ViewHolderImg extends RecyclerView.ViewHolder {
    ImageView ivProfileImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView userName;
    TextView createdAt;
    ImageView ivMediaImage;

    // itemView is a representation of one row in the recycle view, a tweet
    public ViewHolderImg(@NonNull View itemView) {
        super(itemView);
        Log.i("Tweets Adapter", "Created ViewHolderImg");
        ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        tvBody = itemView.findViewById(R.id.tvBody);
        tvScreenName = itemView.findViewById(R.id.tvScreenName);
        userName = itemView.findViewById(R.id.tvName);
        createdAt = itemView.findViewById(R.id.tvCreatedAt);
        ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
    }
}
