package com.carpooler.dao.handlers;

import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.annotations.ElasticData;

import java.io.IOException;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.mapping.PutMapping;

/**
 * Created by raymond on 6/13/15.
 */
public class PutMappingHandler extends AbstractHandler {
    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        Class data = (Class) message.obj;
        if (data==null){
            throw new IllegalArgumentException("data cannot be null");
        }

        ElasticData ed = getElasticData(data);
        String index = ed.index();
        String indexType = ed.type();
        String mapping = ed.mapping();
        PutMapping put = new PutMapping.Builder(index,indexType,mapping).build();
        try {
            JestResult result = client.execute(put);
            if (result.isSucceeded()) {
                replySuccess(message, DatabaseService.PUT_MAPPING, result.getJsonString());
            }else{
                replyError(message, result);
            }
        } catch (IOException e) {
            replyError(message,e);
        }
    }

}
