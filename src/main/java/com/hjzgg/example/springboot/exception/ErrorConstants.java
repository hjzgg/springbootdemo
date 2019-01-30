package com.hjzgg.example.springboot.exception;

/**
 * 常量类
 */
public interface ErrorConstants {

    public static final String RE_DELIMERS = "\\s*[,;]\\s*";

    // 异常错误码
    String ERROR_CODE = "errorCode";
    // 默认异常错误码
    String DEFAULT_ERROR_CODE = "000000";
    // 异常提示信息
    String ERROR_MSG = "errorMsg";
    // 默认异常提示信息
    String DEFAULT_ERROR_MSG = "业务异常未定义！";
    // 默认省份编码
    String DEFAULT_PROVINCECODE = "000";


    /***
     * 系统异常相关的全局常量
     *
     */
    interface SYSTEM {
        /**
         * 系统严重告警
         */
        String TERRIBLE = "050000";
        /**
         * 系统主要异常
         */
        String PRINCIPAL = "040000";
        /**
         * 异常类型无法实例化
         */
        String PRINCIPAL_CLASSNOTFOUND= "041000";
        /**
         * 系统一般异常
         */
        String COMMON = "030000";
        /**
         * 系统提示异常
         */
        String TIP = "020000";
        /**
         * 系统未知异常
         */
        String UNKNOWN = "010000";
        /**
         * Dubbo类异常前缀
         * 051000：未知异常；
         * 051001：Dubbo调用网络异常；
         * 051002：Dubbo调用超时异常
         * 051003：用户自定义Dubbo异常；
         * 051004：没有服务提供者或者服务提供者被禁用；
         * 051005：Dubbo序列化异常；
         */
        String DUBBO_PREFIX = "05100";
        /**
         * JDK类异常前缀
         */
        String JDK_PREFIX = "05200";
        /**
         * NullPointerException
         */
        String JDK_NPE = "052001";
        /**
         * IndexOutOfBoundsException
         */
        String JDK_IOBE = "052002";
        /**
         * ArithmeticException
         */
        String JDK_ARIE = "052003";
        /**
         * MissingResourceException
         */
        String JDK_MRE = "052004";
        /**
         * ConcurrentModificationException
         */
        String JDK_CME = "052005";
        /**
         * ClassCastException
         */
        String JDK_CCE = "052006";
        /**
         * NoSuchElementException
         */
        String JDK_NSE = "052007";
        /**
         * UnsupportedOperationException
         */
        String JDK_UOE = "052008";
        /**
         * IllegalStateException
         */
        String JDK_ISE = "052009";
        /**
         * IllegalArgumentException
         */
        String JDK_IAE = "052010";
        /**
         * NoSuchMethodException
         */
        String JDK_NSME = "052011";

        /**
         * 其他异常前缀
         */
        String OTHER_PREFIX = "05300";
    }

    /***
     * DB异常相关的全局常量
     *
     */
    interface DATABASE {
        // 数据访问异常前缀
        String DATAACCESS_CLASS = "org.springframework.dao";
        String DATAACCESS_JDBC_CLASS = "org.springframework.jdbc";
        // 数据访问异常
        String DATAACCESS = "250000";
        // 事务处理异常前缀
        String TRANSACTION_CLASS = "org.springframework.transaction";
        // 事务处理异常
        String TRANSACTION = "251000";
    }

}
