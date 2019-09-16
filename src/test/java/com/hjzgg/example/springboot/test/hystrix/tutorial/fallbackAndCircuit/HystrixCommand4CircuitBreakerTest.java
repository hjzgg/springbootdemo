package com.hjzgg.example.springboot.test.hystrix.tutorial.fallbackAndCircuit;

import java.io.IOException;
import java.util.Map;

import com.netflix.hystrix.*;
import org.junit.Test;

/**
 * 
 * CircuitBreakerRequestVolumeThreshold设置为3，意味着10s内请求超过3次就触发熔断器
 * run()中无限循环使命令超时进入fallback，执行3次run后，将被熔断，进入降级，即不进入run()而直接进入fallback
 * 如果未熔断，但是threadpool被打满，仍然会降级，即不进入run()而直接进入fallback
 */
public class HystrixCommand4CircuitBreakerTest extends HystrixCommand<String> {

    private final String name;

    public HystrixCommand4CircuitBreakerTest(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CircuitBreakerTestGroup"))  // define command thread pool
                .andCommandKey(HystrixCommandKey.Factory.asKey("CircuitBreakerTestKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("CircuitBreakerTest"))
                .andThreadPoolPropertiesDefaults(	// 配置线程池
                		HystrixThreadPoolProperties.Setter()
                		.withCoreSize(200)	// 配置线程池里的线程数，设置足够多线程，以防未熔断却打满threadpool
                )
                .andCommandPropertiesDefaults(	// 配置熔断器
                		HystrixCommandProperties.Setter()
                		.withCircuitBreakerEnabled(true)  //  熔断器在整个统计时间内是否开启的阀值
                		.withCircuitBreakerRequestVolumeThreshold(3)    // 至少有3个请求才进行熔断错误比率计算
                		.withCircuitBreakerErrorThresholdPercentage(50)   //当出错率超过50%后熔断器启动
						.withMetricsRollingStatisticalWindowInMilliseconds(5000)   // 统计滚动的时间窗口
						.withCircuitBreakerSleepWindowInMilliseconds(2000)   // 熔断器工作时间，超过这个时间，先放一个请求进去，成功的话就关闭熔断，失败就再等一段时间
//                		.withCircuitBreakerForceOpen(true)	// 置为true时，所有请求都将被拒绝，直接到fallback
//                		.withCircuitBreakerForceClosed(true)	// 置为true时，将忽略错误
//                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)	// 信号量隔离
//                		.withExecutionTimeoutInMilliseconds(5000)
                )
        );
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
    	System.out.println("running run():" + name);
    	int num = Integer.valueOf(name);
    	if(num % 2 == 0) {	// 直接返回
    		return name;
    	} else {
    		timeout();  // 无限循环模拟超时
//			throwException();
    	}
		return name;
    }

    public void timeout() {
		int j = 0;
		while (true) {
			j++;
		}
	}

	public void throwException() {
    	throw new RuntimeException("exception occurs");
	}

	/**
	 * Whether or not your command has a fallback,
	 * all of the usual Hystrix state and circuit-breaker state/metrics are updated to indicate the command failure.
	 */
    @Override
    protected String getFallback() {
        return "CircuitBreaker fallback: " + name;
    }

    public static class UnitTest {

        @Test
        public void testSynchronous() throws IOException {
        	for(int i = 0; i < 50000; i++) {
	        	try {
	        		System.out.println("===========" + new HystrixCommand4CircuitBreakerTest(String.valueOf(i)).execute());
					// 查看熔断器是否打开
					System.out.println(HystrixCircuitBreaker.Factory.getInstance(HystrixCommandKey.Factory.asKey("CircuitBreakerTestKey")).isOpen());
//	        		try {
//	            		TimeUnit.MILLISECONDS.sleep(1000);
//	            	}catch(Exception e) {}
//	        		Future<String> future = new HystrixCommand4CircuitBreakerTest("Hlx"+i).queue();
//	        		System.out.println("===========" + future);
	        	} catch(Exception e) {
	        		System.out.println("run()抛出HystrixBadRequestException时，被捕获到这里" + e.getCause());
	        	}
        	}

        	System.out.println("------开始打印现有线程---------");
        	Map<Thread, StackTraceElement[]> map= Thread.getAllStackTraces();
        	for (Thread thread : map.keySet()) {
				System.out.println(thread.getName());
			}
        	System.out.println("thread num: " + map.size());
        	
        	System.in.read();
        }
    }

}