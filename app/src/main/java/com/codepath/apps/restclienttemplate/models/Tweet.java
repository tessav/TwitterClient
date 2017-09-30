package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by tessavoon on 9/27/17.
 */

@Parcel
public class Tweet {

    public String body;
    public long uid;
    public User user;
    public String createdAt;
    public String imageUrl;

    public Tweet() {

    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
        if ((media.length() > 0) && media.getJSONObject(0).getString("type").equals("photo")) {
            tweet.imageUrl = media.getJSONObject(0).getString("media_url");
        }
        return tweet;
    }
}
