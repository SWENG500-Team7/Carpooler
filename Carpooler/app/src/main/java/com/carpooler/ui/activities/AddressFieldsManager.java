package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carpooler.trips.AddressErrorCallback;

/**
 * Created by raymond on 7/16/15.
 */
public abstract class AddressFieldsManager implements TextView.OnEditorActionListener{
    private final EditText startAddressEditText;
    private final EditText endAddressEditText;
    private final AddressSearchCallback startAddressCallback;
    private final AddressSearchCallback endAddressCallback;

    public AddressFieldsManager(View view,int startAddressId, int endAddressId){
        startAddressEditText = (EditText) view.findViewById(startAddressId);
        endAddressEditText = (EditText) view.findViewById(endAddressId);
        startAddressEditText.setOnEditorActionListener(this);
        endAddressEditText.setOnEditorActionListener(this);
        startAddressCallback = new AddressSearchCallback(startAddressEditText);
        endAddressCallback = new AddressSearchCallback(endAddressEditText);
    }



    protected abstract Activity getActivity();
    protected abstract void checkSave();
    protected abstract void setStartLocation(String address,AddressErrorCallback callback) throws RemoteException;
    protected abstract void setEndLocation(String address,AddressErrorCallback callback) throws RemoteException;
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_UP) {
            try {
                if (v.getId()==startAddressEditText.getId()) {
                    setStartLocation(startAddressEditText.getText().toString(), startAddressCallback);
                } else {
                    setEndLocation(endAddressEditText.getText().toString(), endAddressCallback);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;


    }

    private class AddressSearchCallback implements AddressErrorCallback{
        private final EditText addressText;


        private AddressSearchCallback(EditText addressText) {
            this.addressText = addressText;
        }

        @Override
        public void doError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            addressText.requestFocus();
        }

        @Override
        public void doException(Exception exception) {
            doError(exception.getMessage());
        }

        @Override
        public void doSuccess(String address) {
            addressText.setFocusable(false);
            addressText.setFocusableInTouchMode(false);
            addressText.setText(address);
            addressText.setFocusable(true);
            addressText.setFocusableInTouchMode(true);
            checkSave();
        }
    }
}

