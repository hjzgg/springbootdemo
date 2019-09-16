package com.hjzgg.example.springboot.test.hystrix.tutorial.isolation;

import java.io.IOException;
import java.util.Map;

import com.hjzgg.example.springboot.hystrix.HelloWorldHystrixCommand;
import org.junit.Test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;

/**
 * 就是采用资源计数法，
 * 每一个线程来了就去资源池判断一下是否有可用资源，有就继续执行，然后资源池信号量自减，使用完再自增回来；
 * 没有则调用降级策略或抛出异常。
 *
 * 测试信号量隔离
 * 默认执行run()用的是主线程，为了模拟并行执行command，这里我们自己创建多个线程来执行command
 * 设置ExecutionIsolationSemaphoreMaxConcurrentRequests为3，意味着信号量最多允许执行run的并发数为3，超过则触发降级，即不执行run而执行getFallback
 * 设置FallbackIsolationSemaphoreMaxConcurrentRequests为1，意味着信号量最多允许执行fallback的并发数为1，超过则抛异常fallback execution rejected
 */
public class HystrixCommand4SemaphoreTest extends HystrixCommand<String> {

    private final String name;

    public HystrixCommand4SemaphoreTest(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SemaphoreTestGroup"))  
                .andCommandKey(HystrixCommandKey.Factory.asKey("SemaphoreTestKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SemaphoreTestThreadPoolKey"))
                .andCommandPropertiesDefaults(	// 配置信号量隔离
                		HystrixCommandProperties.Setter()
                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE) // 配置信号量隔离
                		.withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                		.withFallbackIsolationSemaphoreMaxConcurrentRequests(10) // fallback(降级)调用最大的并发数
                )
             // 设置了信号量隔离后，线程池配置将变无效
//                .andThreadPoolPropertiesDefaults(	
//                		HystrixThreadPoolProperties.Setter()
//                		.withCoreSize(13)	// 配置线程池里的线程数
//                )
        );
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
    	return "run(): name="+name+"，线程名是" + Thread.currentThread().getName();
    }

    @Override
    protected String getFallback() {
    	return "getFallback(): name="+name+"，线程名是" + Thread.currentThread().getName();
    }

    public static class UnitTest {

        @Test
        public void testSynchronous() throws IOException {
        	
        	try {
        		Thread.sleep(2000);
	        	for(int i = 0; i < 5; i++) {
	        		final int j = i;
	        		// 自主创建线程来执行command，创造并发场景
	                Thread thread = new Thread(new Runnable() {
//	                    @Override  
	                    public void run() {
	                    	// 这里执行两类command：HystrixCommand4SemaphoreTest设置了信号量隔离、HelloWorldHystrixCommand未设置信号量
//	                        System.out.println("-----------" + new HelloWorldHystrixCommand("HLX" + j).execute());
	                        System.out.println("===========" + new HystrixCommand4SemaphoreTest("HLX" + j).execute());	// 被信号量拒绝的线程从这里抛出异常
//	                        System.out.println("-----------" + new HelloWorldHystrixCommand("HLX" + j).execute());	// 被信号量拒绝的线程不能执行到这里
	                        
	                    }  
	                });  
	                thread.start();
	        	}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
//        	try {
//        		TimeUnit.MILLISECONDS.sleep(2000);
//        	}catch(Exception e) {}
        	System.out.println("------开始打印现有线程---------");
        	Map<Thread, StackTraceElement[]> map=Thread.getAllStackTraces();
        	for (Thread thread : map.keySet()) {
				System.out.println(thread.getName());
			}
        	System.out.println("thread num: " + map.size());
        	System.in.read();
        }

        @Test
		public void testSemaphoreCircuit() throws Exception {
        	for (int i  = 0; i < 10; i++) {
				/**
				 * 创建并发场景测试信号量隔离的熔断
				 */
				HystrixCommand4SemaphoreTest semaphoreTest = new HystrixCommand4SemaphoreTest("HLX" + i);
        		Thread th = new Thread(() -> {
					System.out.println(semaphoreTest.execute());
				});
        		th.start();
			}
			System.in.read();
		}
    }

}