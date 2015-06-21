package com.carpooler.dao;

import com.carpooler.dao.dto.DatabaseObject;

/**
 * Created by raymond on 6/20/15.
 */
public class QueryRequest<T extends DatabaseObject> extends ElasticDataRequest<T>{
    private final String query;

    public QueryRequest(String query,Class type) {
        super(type);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
