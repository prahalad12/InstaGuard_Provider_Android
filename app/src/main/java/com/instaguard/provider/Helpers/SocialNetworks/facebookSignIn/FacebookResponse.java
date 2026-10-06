package com.instaguard.provider.Helpers.SocialNetworks.facebookSignIn;

public interface FacebookResponse {
    void onFbSignInFail();

    void onFbSignInSuccess();

    void onFbProfileReceived(FacebookUser facebookUser);

    void onFBSignOut();
}
