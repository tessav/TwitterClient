package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.ivPostImage) ImageView ivPostImage;
    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivBack) ImageView ivBack;
    @BindView(R.id.fabReply) FloatingActionButton fabReply;
    @BindView(R.id.layout) RelativeLayout rl;
    @BindView(R.id.vvPostVideo) VideoView vvPostVideo;

    private final int REQUEST_CODE = 20;
    Context context;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);
        context = getApplicationContext();
        setupToolbar();
        populateView();
        enableBackButton();
        attachFABListener();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void populateView() {
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        ParseRelativeDate dateParser = new ParseRelativeDate();
        tvTimeStamp.setText(dateParser.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .centerCrop().crossFade()
                .transform(new CircleTransform(context))
                .into(ivProfileImage);
        if (tweet.mediaType == Tweet.MediaType.IMAGE) {
            Glide.with(context)
                    .load(tweet.mediaUrl)
                    .crossFade()
                    .transform(new RoundedCornersTransformation(context, 25, 0))
                    .into(ivPostImage);
        } else if (tweet.mediaType == Tweet.MediaType.VIDEO) {
            vvPostVideo.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(tweet.mediaUrl);
            vvPostVideo.setVideoURI(uri);
            vvPostVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    vvPostVideo.start();
                }
            });
        }
    }

    private void enableBackButton() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attachFABListener() {
        fabReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                composeTweet();
            }
        });
    }

    private void composeTweet() {
        Intent i = new Intent(TweetDetailActivity.this, ComposeActivity.class);
        i.putExtra("isReply", true);
        i.putExtra("statusId", String.valueOf(tweet.uid));
        i.putExtra("screenName", tweet.user.screenName);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Snackbar.make(rl, R.string.finish_compose, Snackbar.LENGTH_LONG)
                    .show();
        }
    }


}
