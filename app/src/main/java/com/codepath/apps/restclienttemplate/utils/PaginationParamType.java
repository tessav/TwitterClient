package com.codepath.apps.restclienttemplate.utils;

public enum PaginationParamType {
    SINCE("since_id"),
    MAX("max_id");

    private String param;

    PaginationParamType(String param) {
        this.param = param;
    }

    public String param() {
        return param;
    }

}
