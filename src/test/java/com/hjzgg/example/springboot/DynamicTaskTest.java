package com.hjzgg.example.springboot;

import com.hjzgg.example.springboot.task.DynamicTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hujunzheng
 * @create 2019-01-30 10:43
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicTaskTest {

    @Autowired
    private DynamicTask dynamicTask;

    @Test
    public void test() throws InterruptedException {
        List<DynamicTask.TaskConstant> taskConstans = dynamicTask.getTaskConstants();
        DynamicTask.TaskConstant taskConstant = new DynamicTask.TaskConstant();
        taskConstant.setCron("0/30 * * * * ?");
        taskConstant.setTaskId("test1");
        taskConstans.add(taskConstant);

        TimeUnit.SECONDS.sleep(40);
        taskConstant = taskConstans.get(0);
        taskConstant.setCron("0 0/1 * * * ?");
        taskConstant.setTaskId("test1");


        TimeUnit.MINUTES.sleep(10);
    }
}