package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codepath.apps.restclienttemplate.models.TweetDraft;

/**
 * Created by tessavoon on 9/30/17.
 */

public class SharedPreferenceHelper {
    private static SharedPreferenceHelper instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String POST_BODY = "postBody";
    public static final String IS_REPLY = "isReply";
    public static final String STATUS_ID = "statusId";
    public static final String SCREEN_NAME = "screenName";

    public static SharedPreferenceHelper getInstance(Context context) {
        return (instance == null) ? new SharedPreferenceHelper(context) : instance;
    }

    private SharedPreferenceHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public boolean hasDraft() {
        boolean isPresent = !sharedPreferences.getString(POST_BODY, "").isEmpty();
        return isPresent;
    }

    public TweetDraft getDraft() {
        TweetDraft tweetDraft = new TweetDraft();
        tweetDraft.postBody = sharedPreferences.getString(POST_BODY, "");
        tweetDraft.isReply = sharedPreferences.getBoolean(IS_REPLY, false);
        if (tweetDraft.isReply) {
            tweetDraft.statusId = sharedPreferences.getString(STATUS_ID, "");
            tweetDraft.screenName = sharedPreferences.getString(SCREEN_NAME, "");
        }
        return tweetDraft;
    }

    public void saveDraft(TweetDraft tweetDraft) {
        editor.putString(POST_BODY, tweetDraft.postBody);
        editor.putBoolean(IS_REPLY, tweetDraft.isReply);
        editor.putString(STATUS_ID, tweetDraft.statusId);
        editor.putString(SCREEN_NAME, tweetDraft.screenName);
        editor.apply();
    }

    public void clearDraft() {
        editor.remove(POST_BODY);
        editor.remove(IS_REPLY);
        editor.remove(STATUS_ID);
        editor.remove(SCREEN_NAME);
        editor.apply();
    }

}
