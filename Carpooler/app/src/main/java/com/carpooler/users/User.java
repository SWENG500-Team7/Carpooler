package com.carpooler.users;

import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;

/**
 * User class keeps track of user google identity and payment information
 *
 * Created by Kevin on 6/4/2015.
 */
public class User {

    private String mGoogleId;
    private String mPayCredential;
    private UserDataService userDataService;
    private UserData userData;

    public User(String pGoogleId, UserDataService userDataService) {
        mGoogleId = pGoogleId;
        this.userDataService = userDataService;
        loadUserData();
    }

    private void loadUserData()  {
        try {
            userDataService.getUserData(mGoogleId, new GetUserCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void refreshUserData(){
        loadUserData();
    }
    //region Getters and Setters
    public String getGoogleId() { return mGoogleId; }

    public String getPayCredential() { return mPayCredential; }

    public void setPayCredential(String payCredential) {
        this.mPayCredential = payCredential;
    }
    //endregion

    public void saveUser(){
        try {
            userDataService.createUser(userData, new CreateUserCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private class CreateUserCallback implements DatabaseService.IndexCallback{

        @Override
        public void doError(String message) {
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(String data) {
            refreshUserData();
        }
    }
    private class GetUserCallback implements DatabaseService.GetCallback<UserData>{

        @Override
        public void doError(String message) {
            userData = new UserData();
            userData.setUserId(mGoogleId);
            saveUser();
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(UserData data) {
            userData = data;
        }
    }
}
