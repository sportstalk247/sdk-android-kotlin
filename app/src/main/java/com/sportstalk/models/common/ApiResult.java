package com.sportstalk.models.common;

public class ApiResult<T> {
    /** any message  **/
    private String message;
    /** error information **/
    private T errors;
    /** error code **/
    private int code;
    /** response data **/
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getErrors() {
        return errors;
    }

    public void setErrors(T errors) {
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
