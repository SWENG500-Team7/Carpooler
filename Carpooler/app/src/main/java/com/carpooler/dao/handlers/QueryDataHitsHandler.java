package com.carpooler.dao.handlers;

import com.carpooler.dao.DatabaseService;

import io.searchbox.client.JestResult;
import io.searchbox.core.SearchResult;

/**
 * Created by raymond on 7/16/15.
 */
public class QueryDataHitsHandler extends QueryDataHandler {
    @Override
    public int getWhat() {
        return DatabaseService.QUERY_INDEX_WITH_HITS;
    }

    @Override
    protected Object getResponse(JestResult result, Class type) {
        return ((SearchResult)result).getHits(type);
    }
}
