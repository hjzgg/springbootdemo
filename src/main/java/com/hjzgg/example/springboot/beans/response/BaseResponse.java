package com.hjzgg.example.springboot.beans.response;


public class BaseResponse {
    protected RestStatus restStatus;

    private String returnMessage;

    private String returnCode;

    private Object data;

    private Object errors;

    public BaseResponse() {
        this(RestStatus.FAIL_50001);
    }

    public BaseResponse(RestStatus restStatus) {
        this.restStatus = restStatus;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setRestStatus(RestStatus restStatus) {
        this.restStatus = restStatus;
        this.returnCode = String.valueOf(restStatus.getStatus());
        this.returnMessage = restStatus.getMessage();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
