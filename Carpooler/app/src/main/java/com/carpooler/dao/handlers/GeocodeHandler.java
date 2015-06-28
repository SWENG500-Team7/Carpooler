package com.carpooler.dao.handlers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;

import java.io.IOException;
import java.util.List;

import io.searchbox.client.JestClient;

/**
 * Created by raymond on 6/27/15.
 */
public class GeocodeHandler extends AbstractHandler {
    private final Geocoder geocoder;
    public GeocodeHandler(Context context) {
        geocoder = new Geocoder(context);
    }


    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        DatabaseService.CallbackMessage callbackMessage = (DatabaseService.CallbackMessage) message.obj;
        String address = (String) callbackMessage.getRequest();
        try {
            List<Address> addresses = geocoder.getFromLocationName(address,1);
            if (!address.isEmpty()){
                Address foundAddress = addresses.get(0);
                replySuccess(message,foundAddress,callbackMessage);
            }else{
                replyError(message, "Address not found", callbackMessage);
            }
        } catch (IOException e) {
            replyError(message,e,callbackMessage);
        }
    }

    @Override
    public int getWhat() {
        return DatabaseService.GEOCODE;
    }

    @Override
    public boolean isJestRequired() {
        return false;
    }
}
