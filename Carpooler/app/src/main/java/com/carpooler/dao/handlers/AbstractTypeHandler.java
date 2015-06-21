package com.carpooler.dao.handlers;

import android.os.Message;
import android.os.RemoteException;

import com.carpooler.dao.ElasticDataRequest;
import com.carpooler.dao.annotations.ElasticData;

import java.io.IOException;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;

/**
 * Created by raymond on 6/20/15.
 */
public abstract class AbstractTypeHandler extends AbstractHandler {
    @Override
    public void process(JestClient client, Message message) throws RemoteException {
        ElasticDataRequest request = (ElasticDataRequest) message.obj;
        Class type = request.getType();
        if (type == null) {
            throw new IllegalArgumentException("data cannot be null");
        }

        ElasticData ed = getElasticData(type);
        String index = ed.index();
        String indexType = ed.type();
        AbstractAction target = createTarget(request, index, indexType);
        try {
            JestResult result = client.execute(target);
            if (result.isSucceeded()) {
                Object response = getResponse(result, type);
                replySuccess(message, response);
            } else {
                replyError(message, result);
            }
        } catch (IOException e) {
            replyError(message, e);
        }
    }

    protected abstract Object getResponse(JestResult result, Class type);

    protected abstract AbstractAction createTarget(ElasticDataRequest request, String index, String indexType);
}
