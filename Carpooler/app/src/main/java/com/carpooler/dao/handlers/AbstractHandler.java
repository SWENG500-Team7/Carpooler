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

    protected void replySuccess(Message message, int what, Object obj) throws RemoteException {
        Messenger replyTo = message.replyTo;
        if (replyTo != null) {
            Message response = Message.obtain(null, what, obj);
            replyTo.send(response);
        }
    }

    protected void replyError(Message message, Exception error) throws RemoteException {
        Messenger replyTo = message.replyTo;
        if (replyTo != null) {
            Message response = Message.obtain(null, DatabaseService.EXCEPTION, error);
            replyTo.send(response);
        }
    }
    protected void replyError(Message message, JestResult result) throws RemoteException {
        Messenger replyTo = message.replyTo;
        if (replyTo != null) {
            String errorMessage = result.getErrorMessage();
            Message response = Message.obtain(null, DatabaseService.ERROR, errorMessage);
            replyTo.send(response);
        }
    }

    public abstract void process(JestClient client, Message message) throws RemoteException;
}