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
    public String mediaUrl;
    public MediaType mediaType;

    public enum MediaType {
        IMAGE,
        VIDEO,
        NONE
    }

    public Tweet() {

    }

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        String videoUrl = getVideoUrl(jsonObject);
        String imageUrl = getImageUrl(jsonObject);
        if (!videoUrl.isEmpty()) {
            tweet.mediaType = MediaType.VIDEO;
            tweet.mediaUrl = videoUrl;
        } else if (!imageUrl.isEmpty()) {
            tweet.mediaType = MediaType.IMAGE;
            tweet.mediaUrl = imageUrl;
        } else {
            tweet.mediaType = MediaType.NONE;
            tweet.mediaUrl = "";
        }
        return tweet;
    }

    private static String getImageUrl(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            if ((media.length() > 0) && media.getJSONObject(0).getString("type").equals("photo")) {
                return media.getJSONObject(0).getString("media_url");
            }
        }
        return "";
    }

    private static String getVideoUrl(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("extended_entities")) {
            JSONArray media = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
            if ((media.length() > 0) && media.getJSONObject(0).getString("type").equals("video")) {
                return media.getJSONObject(0).getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url"); //.getJSONObject(0).getString("url");
            }
        }
        return "";
    }
}
