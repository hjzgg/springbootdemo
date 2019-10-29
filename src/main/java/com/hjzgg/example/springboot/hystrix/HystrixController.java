package com.hjzgg.example.springboot.hystrix;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.*;
import com.netflix.hystrix.metric.consumer.HystrixDashboardStream;
import com.netflix.hystrix.serial.SerialHystrixDashboardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author hujunzheng
 * @create 2019-09-16 14:57
 * <p>
 * Hystrix配置参考
 * https://github.com/Netflix/Hystrix/wiki/Configuration
 **/
@RestController
@RequestMapping("/hystrix")
public class HystrixController {

    @GetMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public String metrics() {
        JSONArray metrics = new JSONArray();
        SerialHystrixDashboardData.toMultipleJsonStrings(
                new HystrixDashboardStream.DashboardData(
                        HystrixCommandMetrics.getInstances(),
                        HystrixThreadPoolMetrics.getInstances(),
                        HystrixCollapserMetrics.getInstances()
                )
        ).forEach(metric -> metrics.add(JSON.parse(metric)));
        return metrics.toJSONString();
    }

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(
            @Autowired HttpServletRequest request
            , @RequestParam String groupKey
            , @RequestParam String commandKey
            , @RequestParam String poolKey
            , @RequestParam Integer index
    ) {

        String result = new ThreadPoolHystrixCommand(groupKey, commandKey, poolKey, index).setRequest(request).execute();
        return String.format("%s --> %s_%s_%s_%s", index, groupKey, commandKey, poolKey, result);
    }

    @GetMapping(value = "/test2", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test2(
            @RequestParam String groupKey
            , @RequestParam String commandKey
            , @RequestParam Integer index
    ) {

        String result = new SemaphoreHystrixCommand(groupKey, commandKey, index).execute();
        return String.format("%s --> %s_%s_%s", index, groupKey, commandKey, result);
    }


    @GetMapping(value = "/test3", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test3() {
        String result = new SemaphoreTimeoutHystrixCommand().execute();
        return result;
    }

    /**
     * Hystrix在任务启动时会启动另外一个线程HystrixTime去监测任务。如果在TimeOut时间内，任务未完成，
     *      对于线程池模式，会把执行任务的线程设置为中断；
     *      对于信号量模式，Hystrix不会对执行任务的线程做任何操作。然后再使用HystrixTime线程去执行fallback逻辑。
     * 对于信号量超时模式，如果发生超时，Hystrix任务并不会结束，任务结束还是得依赖于run方法执行完毕。
     */
    private static class SemaphoreTimeoutHystrixCommand extends HystrixCommand<String> {

        public SemaphoreTimeoutHystrixCommand() {
            super(
                    HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("test-timeout"))
                            .andCommandKey(HystrixCommandKey.Factory.asKey("test-timeout"))
                            .andCommandPropertiesDefaults(    // 配置熔断器
                                    HystrixCommandProperties.Setter()
                                            //熔断器在整个统计时间内是否开启的阀值
                                            .withCircuitBreakerEnabled(true)
                                            //至少有3个请求才进行熔断错误比率计算(10s中内最少的请求量，大于该值，断路器配置才会生效)
                                            .withCircuitBreakerRequestVolumeThreshold(3)
                                            //当出错率超过10%后熔断器启动
                                            .withCircuitBreakerErrorThresholdPercentage(10)
                                            //统计滚动的时间窗口，不支持热修改
                                            .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                                            //统计滚动的时间窗口中桶的个数，不支持热修改
                                            .withMetricsRollingStatisticalWindowBuckets(10)
                                            //熔断器工作时间，超过这个时间，先放一个请求进去，成功的话就关闭熔断，失败就再等一段时间
                                            .withCircuitBreakerSleepWindowInMilliseconds(2000)
                                            //配置信号量隔离
                                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                            // execution(命令)调用最大的并发数
                                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                                            //fallback(降级)调用最大的并发数
                                            .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)
                                            //开启超时时间设置
                                            .withExecutionTimeoutEnabled(true)
                                            //设置超时时间
                                            .withExecutionTimeoutInMilliseconds(3000)
                            )
            );
        }

        @Override
        protected String run() throws Exception {
            System.out.println("test-timeout.....");
            TimeUnit.SECONDS.sleep(10);
            return "NORMAL - " + Thread.currentThread().getName();
        }

        @Override
        protected String getFallback() {
            return "FALLBACK - " + Thread.currentThread().getName();
        }
    }

    private static class SemaphoreHystrixCommand extends HystrixCommand<String> {

        private String groupKey;
        private String commandKey;
        private String poolKey;
        private Integer index;

        public SemaphoreHystrixCommand(String groupKey, String commandKey, Integer index) {
            super(
                    HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                            .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                            .andCommandPropertiesDefaults(    // 配置熔断器
                                    HystrixCommandProperties.Setter()
                                            //熔断器在整个统计时间内是否开启的阀值
                                            .withCircuitBreakerEnabled(true)
                                            //至少有3个请求才进行熔断错误比率计算(10s中内最少的请求量，大于该值，断路器配置才会生效)
                                            .withCircuitBreakerRequestVolumeThreshold(3)
                                            //当出错率超过10%后熔断器启动
                                            .withCircuitBreakerErrorThresholdPercentage(10)
                                            //统计滚动的时间窗口，不支持热修改
                                            .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                                            //统计滚动的时间窗口中桶的个数，不支持热修改
                                            .withMetricsRollingStatisticalWindowBuckets(10)
                                            //熔断器工作时间，超过这个时间，先放一个请求进去，成功的话就关闭熔断，失败就再等一段时间
                                            .withCircuitBreakerSleepWindowInMilliseconds(2000)
                                            //配置信号量隔离
                                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                            // execution(命令)调用最大的并发数
                                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                                            //fallback(降级)调用最大的并发数
                                            .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)
                            )
            );
            this.groupKey = groupKey;
            this.commandKey = commandKey;
            this.index = index;
        }

        @Override
        protected String run() throws Exception {
            if (this.index < 20) {
                System.out.println("异常...");
                throw new Exception("异常...");
            }
            return "NORMAL - " + Thread.currentThread().getName();
        }

        @Override
        protected String getFallback() {
            return "FALLBACK - " + Thread.currentThread().getName();
        }
    }

    private static class ThreadPoolHystrixCommand extends HystrixCommand<String> {
        private String groupKey;
        private String commandKey;
        private String poolKey;
        private Integer index;

        private HttpServletRequest request;

        public ThreadPoolHystrixCommand(String groupKey, String commandKey, String poolKey, Integer index) {
            super(
                    HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                            .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
                            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(poolKey))
                            //配置线程池
                            .andThreadPoolPropertiesDefaults(
                                    HystrixThreadPoolProperties.Setter()
                                            // 配置线程池里的线程数，设置足够多线程，以防未熔断却打满threadpool
                                            .withCoreSize(10)
                            )
                            .andCommandPropertiesDefaults(    // 配置熔断器
                                    HystrixCommandProperties.Setter()
                                            //熔断器在整个统计时间内是否开启的阀值
                                            .withCircuitBreakerEnabled(true)
                                            //至少有3个请求才进行熔断错误比率计算(10s中内最少的请求量，大于该值，断路器配置才会生效)
                                            .withCircuitBreakerRequestVolumeThreshold(3)
                                            //当出错率超过10%后熔断器启动
                                            .withCircuitBreakerErrorThresholdPercentage(10)
                                            //统计滚动的时间窗口
                                            .withMetricsRollingStatisticalWindowInMilliseconds(5000)
                                            //熔断器工作时间，超过这个时间，先放一个请求进去，成功的话就关闭熔断，失败就再等一段时间
                                            .withCircuitBreakerSleepWindowInMilliseconds(2000)
                            )
            );
            this.groupKey = groupKey;
            this.commandKey = commandKey;
            this.poolKey = poolKey;
            this.index = index;
        }

        @Override
        protected String run() throws Exception {
            System.out.println(this.request.getParameterMap());
            if (this.index < 20) {
                throw new Exception("异常...");
            }
            return "NORMAL - " + Thread.currentThread().getName();
        }

        @Override
        protected String getFallback() {
            return "FALLBACK - " + Thread.currentThread().getName();
        }

        public ThreadPoolHystrixCommand setRequest(HttpServletRequest request) {
            this.request = request;
            return this;
        }
    }
}