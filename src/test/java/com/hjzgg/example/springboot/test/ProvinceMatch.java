package com.hjzgg.example.springboot.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hujunzheng
 * @create 2021-04-06 15:52
 **/
public class ProvinceMatch {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("缺少源省份信息");
            return;
        }

        Map<String, String> provinces =
                Files.lines(new File("D:\\工作内容\\项目相关\\省份编码.txt").toPath())
                        .map(line -> line.split("\\s+"))
                        .collect(Collectors.toMap(
                                ps -> ps[0]
                                , ps -> ps[1]
                        ));

        System.out.println(Arrays.stream(args)
                .map(key -> provinces.get(key))
                .collect(Collectors.joining(",")));
    }
}