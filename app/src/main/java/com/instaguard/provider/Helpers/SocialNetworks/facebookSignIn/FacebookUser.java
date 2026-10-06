package com.instaguard.provider.Helpers.SocialNetworks.facebookSignIn;

import org.json.JSONObject;

public class FacebookUser {
    public String name;

    public String firstname;

    public String lastname;

    public String email;

    public String facebookID;

    public String gender;

    public String about;

    public String bio;

    public String coverPicUrl;

    public String profilePic;

    /**
     * JSON response received. If you want to parse more fields.
     */
    public JSONObject response;

}
