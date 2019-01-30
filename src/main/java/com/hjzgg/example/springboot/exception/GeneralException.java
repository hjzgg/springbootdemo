package com.hjzgg.example.springboot.exception;

/**
 * 统一异常基础类
 * <p>
 * <p>所有业务或系统级的异常必须继承该类或其子类</p>
 */
public class GeneralException extends Exception {

    /**
     * 异常错误码
     */
    private String errorCode;
    /**
     * 省份编码
     */
    private String provinceCode;

    public GeneralException(String errorCode) {
        this.errorCode = errorCode;
    }


    public GeneralException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GeneralException(String errorCode, Throwable cause) {
        this(errorCode, errorCode, cause);
    }

    public GeneralException(String errorCode, String message, String provinceCode) {
        super(message);
        this.errorCode = errorCode;
        this.provinceCode = provinceCode;
    }

    public GeneralException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public GeneralException(String errorCode, String message, String provinceCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        if (provinceCode == null || "".equalsIgnoreCase(provinceCode)) {
            return ErrorConstants.DEFAULT_PROVINCECODE;
        } else {
            return provinceCode;
        }
    }

    public String getErrorCode() {
        if (errorCode == null || "".equalsIgnoreCase(errorCode)) {
            return ErrorConstants.DEFAULT_ERROR_CODE;
        } else {
            return errorCode;
        }
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null || "".equalsIgnoreCase(message)) {
            if (ExceptionEnv.isLoaded()) {
                message = ExceptionEnv.getString(getErrorCode(), null);
            }else {
                message = ErrorConstants.DEFAULT_ERROR_MSG;
            }
        }
        return message;
    }

}
