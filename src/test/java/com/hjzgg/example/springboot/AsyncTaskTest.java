package com.hjzgg.example.springboot;

import com.hjzgg.example.springboot.async.MyAsyncTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author hujunzheng
 * @create 2019-03-13 15:37
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTaskTest {
    @Autowired
    private MyAsyncTask myAsyncTask;

    @Test
    public void testAsyncTask() throws InterruptedException {
//        myAsyncTask.async();
//
//        myAsyncTask.asyncInner();
//
//        myAsyncTask.asyncWrapped();
//
//        myAsyncTask.asyncWrappedWithRetry();

        myAsyncTask.asyncWrappedWithRetry2();

        TimeUnit.MINUTES.sleep(5);
    }
}