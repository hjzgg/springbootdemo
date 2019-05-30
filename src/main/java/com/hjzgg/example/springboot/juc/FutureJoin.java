package com.hjzgg.example.springboot.juc;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author hujunzheng
 * @create 2019-05-28 15:50
 **/
public class FutureJoin {
    private static final ExecutorService es = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
//        testCompletionService();
        testCompletableFuture4();
    }

    /**
     * 谁先执行完谁先输出
     */
    private static void testCompletableFuture() {
        List<CompletableFuture<Integer>> futures = Stream
                .iterate(0, i -> i + 1)
                .limit(20)
                .map(i -> CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                System.out.println("开始执行..." + i);
                                TimeUnit.MILLISECONDS
                                        .sleep(ThreadLocalRandom.current().nextInt(5000));
                                System.out.println("结束执行..." + i);
                            } catch (InterruptedException e) {
                                //TODO
                            }
                            return i;
                        }
                        , es
                ))
                .collect(Collectors.toList());
        StreamSupport.stream(new CompletionOrderSpliterator<>(futures), false)
                .forEach(System.out::println);
    }

    /**
     * 获取第一时间完成的任务
     */
    private static void testCompletableFuture2() {
        List<CompletableFuture<Integer>> futures = Stream
                .iterate(0, i -> i + 1)
                .limit(20)
                .map(i -> CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                System.out.println("开始执行..." + i);
                                TimeUnit.MILLISECONDS
                                        .sleep(ThreadLocalRandom.current().nextInt(5000));
                                System.out.println("结束执行..." + i);
                            } catch (InterruptedException e) {
                                //TODO
                            }
                            return i;
                        }
                        , es
                ))
                .collect(Collectors.toList());
        System.out.println("第一时间完成：" + CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).join());
    }

    /**
     * 按照顺序输出
     */
    private static void testCompletableFuture3() {
        List<CompletableFuture<Integer>> futures = Stream
                .iterate(0, i -> i + 1)
                .limit(20)
                .map(i -> CompletableFuture.supplyAsync(
                        () -> {
                            try {
                                TimeUnit.MILLISECONDS
                                        .sleep(ThreadLocalRandom.current().nextInt(5000));
                            } catch (InterruptedException e) {
                                //TODO
                            }
                            return i;
                        }
                        , es
                ))
                .collect(Collectors.toList());
        futures.stream()
                .map(CompletableFuture::join)
                .forEach(System.out::println);
    }

    /**
     * 谁先执行完谁先输出
     */
    private static void testCompletionService() {
        CompletionService cs = new ExecutorCompletionService(es);
        IntStream.range(1, 20)
                .forEach(i -> cs.submit(
                        () -> {
                            try {
                                TimeUnit.MILLISECONDS
                                        .sleep(ThreadLocalRandom.current().nextInt(5000));
                            } catch (InterruptedException e) {
                                //TODO
                            }
                        }
                        , i
                ));
        IntStream.range(1, 20)
                .forEach(i -> {
                    try {
                        System.out.println(cs.take().get());
                    } catch (Exception e) {
                        //TODO
                    }
                });
    }


    private static void testCompletableFuture4() {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("f1 --> " + "f1");
            sleep(1);
            return "result f1";
        });
        CompletableFuture<String> f2 = f1.thenApply(r -> {
            System.out.println("f2 --> " + r);
            sleep(1);
            return "result f2";
        });
        CompletableFuture<String> f3 = f2.thenApply(r -> {
            System.out.println("f3 --> " + r);
            sleep(1);
            return "result f3";
        });

        CompletableFuture<String> f4 = f1.thenApply(r -> {
            System.out.println("f4 --> " + r);
            sleep(1);
            return "result f4";
        });
        CompletableFuture<String> f5 = f4.thenApply(r -> {
            System.out.println("f5 --> " + r);
            sleep(1);
            return "result f5";
        });
        CompletableFuture<String> f6 = f5.thenApply(r -> {
            System.out.println("f6 --> " + r);
            sleep(1);
            return "result f6";
        });

        try {
            f1.get();
            /**
             * f1 --> f1
             * f4 --> result f1
             * f2 --> result f1
             * f5 --> result f4
             * f3 --> result f2
             * f6 --> result f5
             * */
            //f2.get();
            /**
             * f1 --> f1
             * f4 --> result f1
             * f5 --> result f4
             * f6 --> result f5
             * f2 --> result f1
             * f3 --> result f2
             * */
            //f3.get();
            /**
             * f1 --> f1
             * f4 --> result f1
             * f5 --> result f4
             * f6 --> result f5
             * f2 --> result f1
             * f3 --> result f2
             * */
            //f6.get();
            /**
             * f1 --> f1
             * f4 --> result f1
             * f5 --> result f4
             * f6 --> result f5
             * f2 --> result f1
             * f3 --> result f2
             * */
            //f5.get();
            /**
             * f1 --> f1
             * f4 --> result f1
             * f5 --> result f4
             * f6 --> result f5
             * f2 --> result f1
             * f3 --> result f2
             * */

            sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleep(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}