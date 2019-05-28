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
        testCompletableFuture();
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

}