package com.carpooler.dao.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.searchbox.client.JestClient;

/**
 * Created by raymond on 6/27/15.
 */
public class BitmapLoadHandler extends AbstractHandler{
    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        DatabaseService.CallbackMessage callbackMessage = (DatabaseService.CallbackMessage) message.obj;
        String requestUrl = (String) callbackMessage.getRequest();
        try {
            URL url = new URL(requestUrl);
            URLConnection urlConnection = url.openConnection();
            Bitmap bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
            replySuccess(message,bitmap,callbackMessage);
        } catch (MalformedURLException e) {
            replyError(message,e,callbackMessage);
        } catch (IOException e) {
            replyError(message, e, callbackMessage);
        }
    }

    @Override
    public int getWhat() {
        return DatabaseService.BITMAP;
    }

    @Override
    public boolean isJestRequired() {
        return false;
    }
}
