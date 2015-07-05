package com.carpooler.dao.dto;

/**
 * Created by raymond on 7/4/15.
 */
public class UserReviewData {
    protected static final String MAPPING =
            "{"
                + "\"properties\":{"
                    + "\"userId\":{\"type\":\"string\", \"indexed\":\"not_analyzed\"},"
                    + "\"comment\":{\"type\":\"string\", \"indexed\":\"not_analyzed\"}"
                + "}"
            + "}";
    private String userId;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
