package com.hjzgg.example.springboot.task;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;

/**
 * 根据一定规则停止动态定时任务
 */
@Configuration
public class StopDynamicTask implements SchedulingConfigurer, ApplicationContextAware {
    private static Logger LOGGER = LoggerFactory.getLogger(StopDynamicTask.class);

    private static ScheduledTaskRegistrarHelper scheduledTaskRegistrarHelper;

    private ScheduledTaskRegistrar registrar;

    private ApplicationContext applicationContext;

    private static final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, TimingTask> periodTasks = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        this.registrar = registrar;
        this.registrar.setScheduler(this.applicationContext.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, ScheduledExecutorService.class));
        scheduledTaskRegistrarHelper = new ScheduledTaskRegistrarHelper();
    }

    @PreDestroy
    public void destroy() {
        this.registrar.destroy();
    }

    public static void register(Integer retry
            , Long period
            , String taskId
            , ScheduledRunnable task
            , ScheduledCallback callback) {
        scheduledTaskRegistrarHelper.register(retry, taskId, period, task, callback);
    }

    private class ScheduledTaskRegistrarHelper {
        public void register(Integer retry
                , String taskId
                , Long period
                , ScheduledRunnable task
                , ScheduledCallback callback) {
            //是否可以重置定时任务
            TimingTask preTask = periodTasks.get(taskId);
            if (null != preTask
                    && preTask.reset()
                    && existTask(taskId)) {
                return;
            }

            TimingTask curTask = new TimingTask(retry, taskId, period, task, callback);
            AspectTimingTask aspectTimingTask = applicationContext.getBean(AspectTimingTask.class);
            aspectTimingTask.setTimingTask(curTask);
            ScheduledFuture<?> scheduledFuture = registrar.getScheduler().scheduleAtFixedRate(aspectTimingTask, period);
            scheduledFutures.put(taskId, scheduledFuture);
            periodTasks.put(taskId, curTask);
            LOGGER.info("注册定时任务: " + curTask);
        }

        private boolean existTask(String taskId) {
            return scheduledFutures.containsKey(taskId) && periodTasks.containsKey(taskId);
        }
    }

    @Scope("prototype")
    @Bean
    public AspectTimingTask aspectTimingTask() {
        return new AspectTimingTask();
    }

    @Aspect
    @Component
    public static class ScheduledAspect {
        @Around("target(com.hjzgg.example.springboot.task.StopDynamicTask.AspectTimingTask)")
        public Object executeScheduledWrapped(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            if (proceedingJoinPoint instanceof MethodInvocationProceedingJoinPoint) {
                MethodInvocationProceedingJoinPoint methodJoinPoint = (MethodInvocationProceedingJoinPoint) proceedingJoinPoint;
                Method method = ((MethodSignature) methodJoinPoint.getSignature()).getMethod();
                if (AnnotatedElementUtils.isAnnotated(method, ScheduledTask.class)) {
                    LOGGER.info("电子发票定时任务日志同步...");
                    //其他处理
                }
            }
            return proceedingJoinPoint.proceed();
        }
    }

    public static class AspectTimingTask implements Runnable {
        private TimingTask timingTask;

        @Override
        @ScheduledTask
        public void run() {
            timingTask.process();
        }

        public void setTimingTask(TimingTask timingTask) {
            this.timingTask = timingTask;
        }
    }

    private static class TimingTask {
        //重试次数
        private Integer retry;
        //任务标识
        private String taskId;
        //重试间隔
        private Long period;
        //具体任务
        private ScheduledRunnable task;
        //结束回调
        private ScheduledCallback callback;
        //重试计数
        private AtomicInteger count = new AtomicInteger(0);
        //父线程MDC
        private Map<String, String> curContext;

        public TimingTask(Integer retry, String taskId, Long period, ScheduledRunnable task, ScheduledCallback callback) {
            this.retry = retry;
            this.taskId = taskId;
            this.period = period;
            this.task = task;
            this.callback = callback;
            this.curContext = MDC.getCopyOfContextMap();
        }

        public Long getPeriod() {
            return period;
        }

        public void setPeriod(Long period) {
            this.period = period;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public Integer getRetry() {
            return retry;
        }

        public void setRetry(Integer retry) {
            this.retry = retry;
        }

        public AtomicInteger getCount() {
            return count;
        }

        public boolean reset() {
            for (int cnt = this.count.intValue(); cnt < this.retry; cnt = this.count.intValue()) {
                if (this.count.compareAndSet(cnt, 0)) {
                    return true;
                }
            }
            return false;
        }

        public void process() {
            Map<String, String> preContext = MDC.getCopyOfContextMap();
            try {
                if (this.curContext == null) {
                    MDC.clear();
                } else {
                    // 将父线程的MDC内容传给子线程
                    MDC.setContextMap(this.curContext);
                }
                this.task.run();
                exitTask(false);
            } catch (Exception e) {
                LOGGER.error("定时任务异常..." + this, e);
                if (count.incrementAndGet() >= this.retry) {
                    exitTask(true);
                }
            } finally {
                if (preContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(preContext);
                }
            }
        }

        //定时任务退出
        private void exitTask(boolean execCallback) {
            scheduledFutures.get(this.taskId).cancel(false);
            scheduledFutures.remove(this.getTaskId());
            periodTasks.remove(this.getTaskId());
            LOGGER.info("结束定时任务: " + this);
            if (execCallback && callback != null) {
                callback.call();
            }
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this
                    , ToStringStyle.JSON_STYLE
                    , false
                    , false
                    , TimingTask.class);
        }
    }

    @FunctionalInterface
    public interface ScheduledRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface ScheduledCallback {
        void call();
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface ScheduledTask {
    }
}
