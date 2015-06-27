package com.carpooler.dao.handlers;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.annotations.ElasticData;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;

/**
 * Created by raymond on 6/14/15.
 */
public abstract class AbstractHandler {

    protected ElasticData getElasticData(Class<?> type) {
        if (!type.isAnnotationPresent(ElasticData.class)) {
            throw new IllegalArgumentException("Data not ElasticData");
        }
        ElasticData ed = type.getAnnotation(ElasticData.class);
        return ed;
    }

    private void sendReply(Message message, int replyWhat) throws RemoteException {
        Messenger replyTo = message.replyTo;
        if (replyTo != null) {
            Message response = Message.obtain(null, replyWhat, message.obj);
            replyTo.send(response);
        }

    }
    protected void replySuccess(Message message, Object obj,DatabaseService.CallbackMessage callbackMessage) throws RemoteException {
        callbackMessage.setResponse(obj);
        sendReply(message,message.what);
    }

    protected void replyError(Message message, Exception error,DatabaseService.CallbackMessage callbackMessage) throws RemoteException {
        callbackMessage.setException(error);
        sendReply(message,DatabaseService.EXCEPTION);
    }
    protected void replyError(Message message, JestResult result,DatabaseService.CallbackMessage callbackMessage) throws RemoteException {
        replyError(message,result.getErrorMessage(),callbackMessage);
    }

    public void replyError(Message message, String errorMessage,DatabaseService.CallbackMessage callbackMessage) throws RemoteException {
        callbackMessage.setErrorMessage(errorMessage);
        sendReply(message,DatabaseService.ERROR);
    }
    public abstract void process(JestClient client, Message message) throws RemoteException;

    public abstract int getWhat();

    public boolean isJestRequired(){
        return true;
    }
}