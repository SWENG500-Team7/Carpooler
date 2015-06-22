package com.carpooler.dao;

import com.carpooler.dao.dto.DatabaseObject;

/**
 * Created by raymond on 6/20/15.
 */
public class ElasticDataRequest<T extends DatabaseObject> {
    private final Class<T> type;

    public ElasticDataRequest(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }
}
