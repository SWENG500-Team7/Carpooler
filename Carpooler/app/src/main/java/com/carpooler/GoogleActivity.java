package com.carpooler;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

/**
 * GoogleActivity takes care of Google API client lifecycle in an Activity
 *
 * Created by Kevin on 6/9/2015.
 */
public class GoogleActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google */
    protected GoogleApiClient mGoogleApiClient;

    /* Flag indicating intent in progress preventing further intents */
    protected boolean mIntentInProgress;

    /* Flag indicating if the GoogleActivity should automatically connect on start */
    protected boolean mConnectOnStart = true;

    /**
     * Create GoogleApiClient on Activity creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //By default create Google+ API client for each activity
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /**
     * Connect to Google services on Activity start
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Connect on start only if Activity says otherwise
        if (mConnectOnStart) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Disconnect from Google services on Activity stop
     */
    @Override
    protected void onStop() {
        super.onStop();

        //Disconnect from Google services when Activity ends
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Attempt to notify user and resolve errors if Google connection failed
     * @param result
     */
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                //If a connection failed because of google service issue, get google
                //services to resolve issue with user (e.g. sign in again, select account, etc.)
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                //Intent was canceled before it was sent
                //Return to default state and attempt to connect again
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        } else if (!mIntentInProgress){
            mIntentInProgress = true;

            //Show an error dialog detailing connection failure issue
            int serviceResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            Dialog error = GooglePlayServicesUtil.getErrorDialog(serviceResult, this, 0);
            error.show();
        } else {
            mIntentInProgress = false;
        }
    }

    /**
     * Reconnect if failed connection is resolved
     * @param requestCode
     * @param responseCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //If intent from onConnectionFailed resolved, try to reconnect
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Run once Google connection is established
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        /**************Typically override this method**********************/
        /**************Put functionality onced logged in here**********************/
    }

    /**
     * Reconnect after connection suspended
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }
}
