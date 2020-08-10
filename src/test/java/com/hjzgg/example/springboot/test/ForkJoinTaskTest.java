package com.hjzgg.example.springboot.test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;

/**
 * @author hujunzheng
 * @create 2020-08-10 17:44
 **/
public class ForkJoinTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal();
        giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal2();
    }

    private static void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() throws InterruptedException, ExecutionException {
        long firstNum = 1;
        long lastNum = 1_000_000;
        List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed().collect(Collectors.toList());

        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        long actualTotal = customThreadPool.submit(
                () -> aList.parallelStream().reduce(0L, Long::sum)
        ).get();

        assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
    }

    private static void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal2() {
        long firstNum = 1;
        long lastNum = 1_000_000;
        List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed().collect(Collectors.toList());

        long actualTotal = aList.parallelStream().reduce(0L, Long::sum);

        assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
    }
}