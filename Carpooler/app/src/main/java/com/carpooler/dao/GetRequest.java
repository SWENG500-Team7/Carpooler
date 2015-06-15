package com.carpooler.dao;

import com.carpooler.dao.dto.DatabaseObject;

/**
 * Created by raymond on 6/14/15.
 */
public class GetRequest<T extends DatabaseObject> {
    private final String id;
    private final Class<T> type;

    public GetRequest(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }
}
