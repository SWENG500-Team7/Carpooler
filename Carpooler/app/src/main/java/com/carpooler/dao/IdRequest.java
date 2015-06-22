package com.carpooler.dao;

import com.carpooler.dao.dto.DatabaseObject;

/**
 * Created by raymond on 6/14/15.
 */
public class IdRequest<T extends DatabaseObject> extends ElasticDataRequest<T> {
    private final String id;

    public IdRequest(String id, Class<T> type) {
        super(type);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
