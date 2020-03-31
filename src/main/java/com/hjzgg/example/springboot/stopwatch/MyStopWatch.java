package com.hjzgg.example.springboot.stopwatch;

import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

public class MyStopWatch {
    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TimeUnit.SECONDS.sleep(2);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskInfo().getTimeSeconds());

        stopWatch.start();
        TimeUnit.SECONDS.sleep(2);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskInfo().getTimeSeconds());

        System.out.println(stopWatch.prettyPrint());
    }
}
