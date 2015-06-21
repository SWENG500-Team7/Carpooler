package com.carpooler.dao.handlers;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.ElasticDataRequest;
import com.carpooler.dao.IdRequest;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;

/**
 * Created by raymond on 6/20/15.
 */
public class DeleteDataHandler extends AbstractTypeHandler {

    @Override
    protected Object getResponse(JestResult result, Class type) {
        return result.getValue("_id");
    }

    @Override
    protected AbstractAction createTarget(ElasticDataRequest request, String index, String indexType) {
        IdRequest idRequest = (IdRequest) request;
        return new Delete.Builder(idRequest.getId()).index(index).type(indexType).build();
    }

    @Override
    public int getWhat() {
        return DatabaseService.DELETE_INDEX;
    }
}
