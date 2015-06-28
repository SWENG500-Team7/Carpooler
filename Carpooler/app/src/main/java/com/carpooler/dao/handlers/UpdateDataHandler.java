package com.carpooler.dao.handlers;

import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.annotations.ElasticData;

import java.io.IOException;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Update;

/**
 * Created by raymond on 6/16/15.
 */
public class UpdateDataHandler extends AbstractHandler {
    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        DatabaseService.CallbackMessage callbackMessage = (DatabaseService.CallbackMessage) message.obj;
        Object data = callbackMessage.getRequest();
        if (data==null){
            throw new IllegalArgumentException("data cannot be null");
        }
        Class type = data.getClass();

        ElasticData ed = getElasticData(type);
        String index = ed.index();
        String indexType = ed.type();
        Update update = new Update.Builder(data).index(index).type(indexType).build();
        try {
            JestResult result =  client.execute(update);
            if (result.isSucceeded()) {
                String response = (String) result.getValue("_id");
                replySuccess(message, response,callbackMessage);
            }else{
                replyError(message,result,callbackMessage);
            }
        } catch (IOException e) {
            replyError(message,e,callbackMessage);
        }
    }
    @Override
    public int getWhat() {
        return DatabaseService.UPDATE_INDEX;
    }
}
