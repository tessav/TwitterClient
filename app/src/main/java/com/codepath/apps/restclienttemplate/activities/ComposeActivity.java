package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.btnTweet) Button btnTweet;
    @BindView(R.id.etTweetBody) EditText etTweetBody;
    @BindView(R.id.tvMsgCount) TextView tvMsgCount;
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
        setupMessageListener();
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
