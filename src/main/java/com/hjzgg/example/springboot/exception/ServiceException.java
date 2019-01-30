package com.hjzgg.example.springboot.exception;

/**
 * 服务调用异常基础类(Service层)
 */
public class ServiceException extends GeneralException {

    public ServiceException(String errorCode) {
        super(errorCode);
    }

    public ServiceException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ServiceException(String errorCode, String message) {
        super(errorCode, message);
    }

    public ServiceException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ServiceException(String errorCode, String message, String provinceCode, Throwable cause) {
        super(errorCode, message, provinceCode, cause);
    }

    public ServiceException(String errorCode, String message, String provinceCode) {
        super(errorCode, message, provinceCode);
    }
}
