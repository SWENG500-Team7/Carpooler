package com.carpooler.dao.handlers;

import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.GetRequest;
import com.carpooler.dao.annotations.ElasticData;

import java.io.IOException;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

/**
 * Created by raymond on 6/13/15.
 */
public class GetDataHandler extends AbstractHandler {
    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        GetRequest request = (GetRequest) message.obj;
        Class type = request.getType();
        if (type==null){
            throw new IllegalArgumentException("data cannot be null");
        }

        ElasticData ed = getElasticData(type);
        String index = ed.index();
        String indexType = ed.type();
        String id = request.getId();
        Get get = new Get.Builder(index,id).type(indexType).build();
        try {
            JestResult result = client.execute(get);
            if (result.isSucceeded()) {
                Object response = result.getSourceAsObject(type);
                replySuccess(message, DatabaseService.GET_INDEX, response);
            }else{
                replyError(message, result);
            }
        } catch (IOException e) {
            replyError(message, e);
        }
    }
}
