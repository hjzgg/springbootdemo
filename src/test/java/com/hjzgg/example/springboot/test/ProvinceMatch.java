package com.hjzgg.example.springboot.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author hujunzheng
 * @create 2021-04-06 15:52
 **/
public class ProvinceMatch {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("缺少源省份信息");
            return;
        }
        Map<String, String> provinces = new HashMap<>(32);
        Scanner scanner = new Scanner(PROVINCE_TEXT);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            String[] p = s.split("\\s+");
            provinces.put(p[0], p[1]);
        }
        System.out.println(Arrays.stream(args)
                .map(key -> provinces.get(key))
                .collect(Collectors.joining(",")));
    }

    private static final String PROVINCE_TEXT =
            "北京市         100\n" +
            "广东           200\n" +
            "上海市         210\n" +
            "天津市         220\n" +
            "重庆市         230\n" +
            "辽宁           240\n" +
            "江苏           250\n" +
            "湖北           270\n" +
            "四川           280\n" +
            "陕西           290\n" +
            "河北           311\n" +
            "山西           351\n" +
            "河南           371\n" +
            "吉林           431\n" +
            "黑龙江         451\n" +
            "内蒙古         471\n" +
            "山东           531\n" +
            "安徽           551\n" +
            "浙江           571\n" +
            "福建           591\n" +
            "湖南           731\n" +
            "广西           771\n" +
            "江西           791\n" +
            "贵州           851\n" +
            "云南           871\n" +
            "西藏           891\n" +
            "海南           898\n" +
            "甘肃           931\n" +
            "宁夏           951\n" +
            "青海           971\n" +
            "新疆           991";
}