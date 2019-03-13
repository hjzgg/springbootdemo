package com.hjzgg.example.springboot.async;

import com.hjzgg.example.springboot.utils.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncWrapped {
    protected static Logger LOGGER = LoggerFactory.getLogger(AsyncWrapped.class);

    @Async
    public void asyncProcess(Runnable runnable, Callback callback, Retry retry) {
        try {
            if (retry == null) {
                retry = new Retry(1);
            }
            retry.execute(ctx -> {
                runnable.run();
                return null;
            }, ctx -> {
                if (callback != null) {
                    callback.call();
                }
                return null;
            });
        } catch (Exception e) {
            LOGGER.error("异步调用异常...", e);
        }
    }

    @Async
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public void asyncProcess(Runnable runnable) throws Exception {
        LOGGER.info("重试中...");
        runnable.run();
    }

    @FunctionalInterface
    public interface Runnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface Callback {
        void call();
    }
}
