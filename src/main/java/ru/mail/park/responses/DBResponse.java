package ru.mail.park.responses;

/**
 * Created by Andry on 06.11.16.
 */
public class DBResponse {
    private Integer code;
    private Object object;

    public Integer getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }

    public DBResponse(Integer code, Object object) {
        this.code = code;
        this.object = object;
    }

    public DBResponse(Integer code) {
        this.code = code;
        this.object = "";
    }
}
