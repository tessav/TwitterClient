package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.ivCancel) ImageView ivCancel;

    private TwitterClient client;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
        context = getApplicationContext();
        client = TwitterApp.getRestClient();
        setupToolbar();
        setupSubmitListener();
        setupCancelListener();
        Glide.with(context)
                .load(getIntent().getStringExtra("profileImage"))
                .centerCrop()
                .transform(new CircleTransform(context))
                .into(ivProfileImage);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
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
        String tweet = etTweetBody.getText().toString();
        Intent data = new Intent();
        data.putExtra("code", 20);
        data.putExtra("tweet", tweet);
        setResult(RESULT_OK, data);
        closeForm();
    }

    private void closeForm() {
        this.finish();
    }


}
