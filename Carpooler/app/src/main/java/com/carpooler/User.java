package com.carpooler;

/**
 * User class keeps track of user google identity and payment information
 *
 * Created by Kevin on 6/4/2015.
 */
public class User {

    private String mGoogleId;
    private String mPayCredential;

    public User(String pGoogleId) {
        mGoogleId = pGoogleId;
    }

    //region Getters and Setters
    public String getGoogleId() { return mGoogleId; }

    public String getPayCredential() { return mPayCredential; }

    public void setPayCredential(String payCredential) {
        this.mPayCredential = payCredential;
    }
    //endregion
}
