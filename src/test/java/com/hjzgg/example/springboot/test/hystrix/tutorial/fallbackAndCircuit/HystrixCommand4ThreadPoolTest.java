package com.hjzgg.example.springboot.test.hystrix.tutorial.fallbackAndCircuit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 设置线程池里的线程数＝3，然后循环>3次和<3次，最后查看当前所有线程名称
 */
public class HystrixCommand4ThreadPoolTest extends HystrixCommand<String> {

    private final String name;

    public HystrixCommand4ThreadPoolTest(String name) {
//        super(HystrixCommandGroupKey.Factory.asKey("ThreadPoolTestGroup"));
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ThreadPoolTestGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("testCommandKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolTest"))   // 与其他命名的线程池天然隔离
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(10000)  // 0秒超时
                )
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(10)    // 配置线程池里的线程数
                )
        );
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        /*---------------会触发fallback的case-------------------*/
//    	int j = 0;
//    	while (true) {
//    		j++;
////    		return "a";
//    	}
        // 除零异常
//    	int i = 1/0;

        // 主动抛出异常
//        throw new HystrixTimeoutException();
//        throw new RuntimeException("this command will trigger fallback");
//        throw new Exception("this command will trigger fallback");
//    	throw new HystrixRuntimeException(FailureType.BAD_REQUEST_EXCEPTION, commandClass, message, cause, fallbackException);
        
    	/*---------------不会触发fallback的case-------------------*/
        // 被捕获的异常不会触发fallback
//    	try {
//    		throw new RuntimeException("this command never trigger fallback");
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}

        // HystrixBadRequestException异常由非法参数或非系统错误引起，不会触发fallback，也不会被计入熔断器
//        throw new HystrixBadRequestException("HystrixBadRequestException is never trigger fallback");
        TimeUnit.MILLISECONDS.sleep(5000);
        return name;
    }

    @Override
    protected String getFallback() {
        return "fallback: " + name;
    }

    public static class UnitTest {

        @Test
        public void testSynchronous() throws IOException {
            /**
             * 配置线程池数目为3，然后先用一个for循环执行queue()，
             * 触发的run() sleep 2s，然后再用第2个for循环执行execute()，发现所有execute()都触发了fallback，这是因为第1个for的线程还在sleep，
             * 占用着线程池所有线程，导致第2个for的所有命令都无法获取到线程
             */
            for (int i = 0; i < 10; i++) {
                try {
//	        		assertEquals("fallback: Hlx", new HystrixCommand4ThreadPoolTest("Hlx").execute());
//	        		System.out.println("===========" + new HystrixCommand4ThreadPoolTest("Hlx").execute());
                    Future<String> future = new HystrixCommand4ThreadPoolTest("Hlx" + i).queue();
//	        		System.out.println("===========" + future);
                } catch (Exception e) {
                    System.out.println("run()抛出HystrixBadRequestException时，被捕获到这里" + e.getCause());
                }
            }
            for (int i = 0; i < 20; i++) {
                try {
                    //        		assertEquals("fallback: Hlx", new HystrixCommand4ThreadPoolTest("Hlx").execute());
                    System.out.println(i + " ===========" + new HystrixCommand4ThreadPoolTest("Hlx").execute());
//	        		Future<String> future = new HystrixCommand4ThreadPoolTest("Hlx1"+i).queue();
//	        		System.out.println("===========" + future);
                } catch (Exception e) {
                    System.out.println("run()抛出HystrixBadRequestException时，被捕获到这里" + e.getCause());
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(2000);
            } catch (Exception e) {
            }

            System.out.println("------开始打印现有线程---------");
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            for (Thread thread : map.keySet()) {
                System.out.println(thread.getName());
            }
            System.out.println(map);
            System.out.println("thread num: " + map.size());
//        	int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();
//            System.out.println("num executed: " + numExecuted);
            System.in.read();
        }
    }

}