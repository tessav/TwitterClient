package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Profile;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.PaginationParamType;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    @BindView(R.id.rvTweet) RecyclerView rvTweets;
    @BindView(R.id.fabCompose) FloatingActionButton fabCompose;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    Context context;
    private EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager;
    Profile profile;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        context = getApplicationContext();
        client = TwitterApp.getRestClient();
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        addDividers(linearLayoutManager);
        attachScrollListener(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        populateTimeline(PaginationParamType.SINCE, 1);
        attachFABListener();
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                try {
                    profile = Profile.fromJSON(response);
                    Glide.with(context)
                        .load(profile.profileImageUrl)
                        .centerCrop()
                        .transform(new CircleTransform(context))
                        .into(ivProfileImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    private void addDividers(LinearLayoutManager linearLayoutManager) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                linearLayoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);
    }

    private void attachScrollListener(LinearLayoutManager linearLayoutManager) {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet maxTweet = tweets.get(totalItemsCount - 1);
                populateTimeline(PaginationParamType.MAX, maxTweet.uid - 1);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void attachFABListener() {
        fabCompose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                composeTweet();
            }
        });
    }

    private void composeTweet() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        i.putExtra("profileImage", profile.profileImageUrl);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String tweet = data.getExtras().getString("tweet");
            submitTweet(tweet);
        }
    }

    private void submitTweet(String tweet) {
        client.postTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("post", response.toString());
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    tweets.add(0, tweet);
                    tweetAdapter.notifyItemInserted(0);
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("post", errorResponse.toString());
            }

        });
    }

    private void populateTimeline(PaginationParamType tweetIdType, long tweetId) {
        client.getHomeTimeline(tweetIdType, tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

}
