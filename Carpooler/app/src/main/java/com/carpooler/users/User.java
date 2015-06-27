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

    /**
     * Converts User instance to a UserData object and persists
     * using given database connection
     * @param conn
     * @return
     */
    public boolean persistUser(DatabaseService.Connection conn) {
        //Create User dto
        UserData data = new UserData();
        data.setUserId(mGoogleId);

        //Persist user data
        try {
            //Persist user data
            UserDataService dataService = new UserDataService(conn);
            dataService.createUser(data,null);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
