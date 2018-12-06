package com.bcd.base.message;




import com.bcd.base.json.jackson.filter.SimpleJacksonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.Serializable;

/**
 * Created by dave on 16/2/13.
 * @param <T>
 */
public class JsonMessage<T> implements Serializable{
    private boolean result;
    private String message = "";
    @JsonIgnore
    private String error = "";
    private String code="";
    private T data;

    public JsonMessage() { }

    public JsonMessage(boolean result) {
        this(result,null);
    }

    public JsonMessage(boolean result, String message) {
        this(result, message,null);
    }

    public JsonMessage(boolean result, String message, String code) {
        this(result, message,code,null);
    }

    public JsonMessage(boolean result, String message, String code, String error) {
        this(result, message, code,error,null);
    }

    public JsonMessage(boolean result, String message, String code, String error, T data) {
        this.result=result;
        if(message!=null){
            this.message=message;
        }
        if(code!=null) {
            this.code = code;
        }
        if(error!=null) {
            this.error = error;
        }
        this.data = data;
    }

    public static <T> JsonMessage<T> success() {
        return JsonMessage.success(null);
    }

    public static <T> JsonMessage<T> success(T data) {
        return JsonMessage.success(data,null);
    }

    public static <T> JsonMessage<T> success(T data , String message) {
        return JsonMessage.success(data,message,null);
    }

    public static <T> JsonMessage<T> success(T data, String message, String code) {
        return new JsonMessage<>(true,message,code,"",data);
    }

    public static <T> JsonMessage<T> fail() {
        return JsonMessage.fail(null);
    }

    public static <T> JsonMessage<T> fail(String message) {
        return JsonMessage.fail(message,null);
    }

    public static <T> JsonMessage<T> fail(String message, String code) {
        return JsonMessage.fail(message,code,null);
    }

    public static <T> JsonMessage<T> fail(String message, String code, String error) {
        return new JsonMessage<>(false, message,code,error);
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
