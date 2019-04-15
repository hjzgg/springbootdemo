package com.hjzgg.example.springboot.beans.response;

/**
 * 返回信息枚举类型
 */
public enum RestStatus {
    SUCCESS(0, "成功")
    , FAIL_50001(50001, "服务器内部异常");

    //全局变量，状态
    private int status;
    //全局变量消息message
    private String message;

    //私有构造方法
    RestStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    //get方法
    public static String getMessage(int status) {
        if (status == 0) {
            return SUCCESS.getMessage();
        } else {
            //businessException 业务异常信息
            RestStatus[] values = values();
            int size = values.length;

            for (int i = 0; i < size; i++) {
                RestStatus restStatus = values[i];
                if (restStatus.getStatus() == status) {
                    return restStatus.getMessage();
                }
            }

            //服务器异常信息
            return FAIL_50001.getMessage();
        }
    }


    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
