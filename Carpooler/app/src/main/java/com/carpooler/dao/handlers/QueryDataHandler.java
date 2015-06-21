package com.carpooler.dao.handlers;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.ElasticDataRequest;
import com.carpooler.dao.QueryRequest;

import io.searchbox.action.AbstractAction;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

/**
 * Created by raymond on 6/20/15.
 */
public class QueryDataHandler extends AbstractTypeHandler {

    @Override
    public int getWhat() {
        return DatabaseService.QUERY_INDEX;
    }

    @Override
    protected Object getResponse(JestResult result, Class type) {
        return result.getSourceAsObjectList(type);
    }

    @Override
    protected AbstractAction createTarget(ElasticDataRequest request, String index, String indexType) {
        QueryRequest queryRequest = (QueryRequest) request;
        Search search = new Search.Builder(queryRequest.getQuery()).addIndex(index).addType(indexType).build();
        return search;
    }
}
