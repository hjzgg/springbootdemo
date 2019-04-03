package com.hjzgg.example.springboot;

import com.hjzgg.example.springboot.task.DynamicTask;
import com.hjzgg.example.springboot.task.StopDynamicTask;
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

    @Autowired(required = false)
    private DynamicTask dynamicTask;

    @Test
    public void test() throws InterruptedException {
        List<DynamicTask.TaskConstant> taskConstans = dynamicTask.getTaskConstants();
        DynamicTask.TaskConstant taskConstant = new DynamicTask.TaskConstant();
        taskConstant.setCron("0/5 * * * * ?");
        taskConstant.setTaskId("test1");
        taskConstans.add(taskConstant);


        DynamicTask.TaskConstant taskConstant1 = new DynamicTask.TaskConstant();
        taskConstant1.setCron("0/5 * * * * ?");
        taskConstant1.setTaskId("test2");
        taskConstans.add(taskConstant1);

        DynamicTask.TaskConstant taskConstant2 = new DynamicTask.TaskConstant();
        taskConstant2.setCron("0/5 * * * * ?");
        taskConstant2.setTaskId("test3");
        taskConstans.add(taskConstant2);

        TimeUnit.SECONDS.sleep(40);
        //移除并添加新的配置
        taskConstans.remove(taskConstans.size() - 1);
        DynamicTask.TaskConstant taskConstant3 = new DynamicTask.TaskConstant();
        taskConstant3.setCron("0/5 * * * * ?");
        taskConstant3.setTaskId("test4");
        taskConstans.add(taskConstant3);
//
        TimeUnit.MINUTES.sleep(50);
    }

    @Test
    public void test2() throws InterruptedException {
        StopDynamicTask.register(5, 2 * 1000L, "taskId123", () -> System.out.println("StopDynamicTask Test"), null);
        TimeUnit.SECONDS.sleep(5);
        StopDynamicTask.register(5, 2 * 1000L, "taskId123", () -> System.out.println("StopDynamicTask Test2"), null);
        TimeUnit.SECONDS.sleep(60);
    }
}