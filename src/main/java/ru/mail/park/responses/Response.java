package ru.mail.park.responses;

import java.util.Map;

/**
 * Created by Andry on 06.11.16.
 */
public class Response {
    private int code = Status.OK;
    private Object response;

    public Response(DBResponse reply) {
        this.code = reply.getCode();
        this.response = reply.getObject();
    }

    public Response(Map response) {
        this.response = response;
    }

    public Response(String response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public Object getResponse() {
        return response;
    }
}
