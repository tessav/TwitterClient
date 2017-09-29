package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tessavoon on 9/28/17.
 */

public class Profile {
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public static Profile fromJSON(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();
        profile.name = jsonObject.getString("name");
        profile.screenName = jsonObject.getString("screen_name");
        profile.uid = jsonObject.getLong("id");
        profile.profileImageUrl = jsonObject.getString("profile_image_url");
        return profile;
    }

}
