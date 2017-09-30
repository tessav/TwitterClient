package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by tessavoon on 9/27/17.
 */

@Parcel
public class User {

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public User() {

    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        return user;
    }
}
