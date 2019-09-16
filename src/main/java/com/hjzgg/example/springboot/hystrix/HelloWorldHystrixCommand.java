package com.hjzgg.example.springboot.hystrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class HelloWorldHystrixCommand extends HystrixCommand<String> {

	private final String name;

	private final boolean throwException = false;

	public HelloWorldHystrixCommand(String name) {
//		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("testCommandGroupKey"))  
                .andCommandKey(HystrixCommandKey.Factory.asKey("testCommandKey"))  
                /* 使用HystrixThreadPoolKey工厂定义线程池名称*/
				/**
				 *  A HystrixCommand is associated with a single HystrixThreadPool as retrieved by the HystrixThreadPoolKey injected into it,
				 *  or it defaults to one created using the HystrixCommandGroupKey it is created with.
				 *
				 *  不同command 给予不同的thread pool key
				 */
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("testThreadPool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
//                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)	// 信号量隔离
                		.withExecutionTimeoutInMilliseconds(5000)));
//		HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true);
//		HystrixCollapserProperties.Setter()
//		HystrixThreadPoolProperties.Setter().withCoreSize(1);
        this.name = name;
	}
	
//	@Override  
//  protected String getFallback() {
//		System.out.println("触发了降级!");
//      return "exeucute fallback";
//  }

	/**
	 * 由新创建的线程执行
	 * @return
	 * @throws Exception
	 */
	@Override
	protected String run() throws InterruptedException {
//		for (int i = 0; i < 10; i++) {
//			System.out.println("runing HelloWorldHystrixCommand..." + i);
//		}
//		
//		TimeUnit.MILLISECONDS.sleep(2000);
		System.out.println("HelloWorldHystrixCommand running");
		if (throwException) {
			throw new RuntimeException("One exception occurs");
		}
		return "Hello " + name + "! thread:" + Thread.currentThread().getName();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new HelloWorldHystrixCommand("world").queue().get());  // 阻塞执行
		System.out.println(new HelloWorldHystrixCommand("world").execute());      // 同步执行
	}
}
