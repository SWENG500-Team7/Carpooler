package com.carpooler.dao.handlers;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.ElasticDataRequest;
import com.carpooler.dao.IdRequest;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

/**
 * Created by raymond on 6/13/15.
 */
public class GetDataHandler extends AbstractTypeHandler {

    @Override
    protected Object getResponse(JestResult result, Class type) {
        return result.getSourceAsObject(type);
    }

    @Override
    protected AbstractAction createTarget(ElasticDataRequest request, String index, String indexType) {
        IdRequest idRequest = (IdRequest) request;
        return new Get.Builder(index,idRequest.getId()).type(indexType).build();
    }

    @Override
    public int getWhat() {
        return DatabaseService.GET_INDEX;
    }
}
