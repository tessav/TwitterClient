package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.models.Profile;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.tvMsgCount) TextView tvMsgCount;
    @BindView(R.id.ivCancel) ImageView ivCancel;
    @BindView(R.id.tvReply) TextView tvReply;

    private TwitterClient client;
    Context context;
    Profile profile;
    boolean isReply;
    String screenName;
    long statusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
        context = getApplicationContext();
        client = TwitterApp.getRestClient();
        setupToolbar();
        setupMessageListener();
        setupSubmitListener();
        setupCancelListener();
        getProfileImage();
        processIntent();
    }

    private void processIntent() {
        Intent intent = getIntent();
        isReply = intent.getBooleanExtra("isReply", false);
        if (isReply) {
            screenName = getIntent().getStringExtra("screenName");
            statusId = getIntent().getLongExtra("statusId", 0);
            tvReply.setText(Html.fromHtml("Replying to <font color=\"#1DA1F2\"> @" + screenName + "</font>"));
            setTweetBody("@" + screenName + " ");
        } else {
            String sharedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            String sharedSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            if (sharedUrl != null && sharedSubject != null) {
                setTweetBody(sharedSubject + " " + sharedUrl);
            }
        }
    }

    private void setTweetBody(String text) {
        etTweetBody.setText(text);
        etTweetBody.setSelection(etTweetBody.getText().length());
    }

    private void getProfileImage() {
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

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void setupMessageListener() {
        disableButton();
        etTweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("count", String.valueOf(count));
                int charLeft = 140 - (start + count);
                changeCountFeedback(charLeft);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupSubmitListener() {
        btnTweet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void setupCancelListener() {
        ivCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeForm();
            }
        });
    }

    private void submitForm() {
        if (isReply) {
            replyTweet();
        } else {
            postTweet();
        }
    }

    private void postTweet() {
        client.postTweet(etTweetBody.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("post", response.toString());
                returnResultToParent(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("post", errorResponse.toString());
            }
        });
    }

    private void replyTweet() {
        client.replyTweet(etTweetBody.getText().toString(), statusId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("post", response.toString());
                returnResultToParent(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("post", errorResponse.toString());
            }
        });
    }

    private void returnResultToParent(JSONObject response) {
        try {
            Tweet tweet = Tweet.fromJSON(response);
            Intent data = new Intent();
            data.putExtra("code", 20);
            data.putExtra("tweet", Parcels.wrap(tweet));
            setResult(RESULT_OK, data);
            closeForm();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void closeForm() {
        this.finish();
    }

    private void changeCountFeedback(int charLeft) {
        tvMsgCount.setText(String.valueOf(charLeft));
        if (charLeft == 140) {
            disableButton();
        } else if (charLeft < 0) {
            tvMsgCount.setTextColor(Color.RED);
            disableButton();
        } else {
            tvMsgCount.setTextColor(Color.GRAY);
            enableButton();
        }
    }

    private void disableButton() {
        if (btnTweet.isEnabled()) {
            btnTweet.setBackgroundColor(getResources().getColor(R.color.disabledBtn));
            btnTweet.setEnabled(false);
        }
    }

    private void enableButton() {
        if (!btnTweet.isEnabled()) {
            btnTweet.setBackgroundColor(getResources().getColor(R.color.twitter));
            btnTweet.setEnabled(true);
        }
    }


}
