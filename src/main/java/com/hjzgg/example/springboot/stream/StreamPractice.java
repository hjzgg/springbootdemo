package com.hjzgg.example.springboot.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamPractice {
    public static void main(String[] args) {
        //filter筛选
        List<Integer> integerList = Arrays.asList(1, 1, 2, 3, 4, 5);
        Stream<Integer> stream = integerList.stream().filter(i -> i > 3);
        System.out.println(stream.collect(Collectors.toList()));

        //distinct去重
        integerList = Arrays.asList(1, 1, 2, 3, 4, 5);
        stream = integerList.stream().distinct();
        System.out.println(stream.collect(Collectors.toList()));

        //limit返回指定流个数
        integerList = Arrays.asList(1, 1, 2, 3, 4, 5);
        stream = integerList.stream().limit(3);
        System.out.println(stream.collect(Collectors.toList()));

        //skip跳过流中的元素
        integerList = Arrays.asList(1, 1, 2, 3, 4, 5);
        stream = integerList.stream().skip(2);
        System.out.println(stream.collect(Collectors.toList()));

        //map流映射
        List<String> stringList = Arrays.asList("Java 8", "Lambdas", "In", "Action");
        stream = stringList.stream().map(String::length);
        System.out.println(stream.collect(Collectors.toList()));

        //flatMap流转换,将一个流中的每个值都转换为另一个流
        List<List<Integer>> lists = new ArrayList<List<Integer>>() {{
            add(Arrays.asList(1, 2));
            add(Arrays.asList(3, 4));
        }};
        stream = lists.stream().flatMap(List::stream);
        //stream = lists.stream().flatMap(list -> list.stream());
        System.out.println(stream.collect(Collectors.toList()));

        //allMatch匹配所有
        integerList = Arrays.asList(1, 2, 3, 4, 5);
        if (integerList.stream().allMatch(i -> i > 3)) {
            System.out.println("值都大于3");
        }

        //anyMatch匹配其中一个
        integerList = Arrays.asList(1, 2, 3, 4, 5);
        if (integerList.stream().anyMatch(i -> i > 3)) {
            System.out.println("存在大于3的值");
        }

        //noneMatch全部不匹配
        integerList = Arrays.asList(1, 2, 3, 4, 5);
        if (integerList.stream().noneMatch(i -> i > 3)) {
            System.out.println("值都小于3");
        }

        //findFirst查找第一个
        integerList = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> result = integerList.stream().filter(i -> i > 3).findFirst();
        System.out.println(result.get());

        //findAny随机查找一个
        //通过findAny方法查找到其中一个大于三的元素并打印，因为内部进行优化的原因，当找到第一个满足大于三的元素时就结束，该方法结果和findFirst方法结果一样。提供findAny方法是为了更好的利用并行流，findFirst方法在并行上限制更多
        integerList = Arrays.asList(1, 6, 8, 2, 3, 4, 5);
        result = integerList.parallelStream().filter(i -> i > 3).findAny();
        System.out.println(result.get());

        //统计流中元素个数
        integerList = Arrays.asList(1, 1, 2, 2, 3);
        System.out.println(integerList.stream().count());
        System.out.println(integerList.stream().collect(Collectors.counting()));

        //获取流中最小值
        System.out.println(integerList.stream().min(Integer::compareTo));
        System.out.println(integerList.stream().collect(Collectors.minBy(Integer::compareTo)));

        //获取流中最大值
        System.out.println(integerList.stream().max(Integer::compareTo));
        System.out.println(integerList.stream().collect(Collectors.maxBy(Integer::compareTo)));

        //求和
        System.out.println(integerList.stream().mapToInt(Integer::intValue).sum());
        System.out.println(integerList.stream().collect(Collectors.summingInt(Integer::intValue)));
        System.out.println(integerList.stream().reduce(0, Integer::sum));

        //平均值
        System.out.println(integerList.stream().collect(Collectors.averagingInt(Integer::intValue)));

        //通过summarizingInt同时求总和、平均值、最大值、最小值
        System.out.println(integerList.stream().collect(Collectors.summarizingInt(Integer::intValue)));

        //返回List
        System.out.println(integerList.stream().collect(Collectors.toList()));
        //返回Set
        System.out.println(integerList.stream().collect(Collectors.toSet()));
        //返回Map
        System.out.println(integerList.stream().collect(Collectors.toMap(k -> k, v -> v, (v1, v2) -> v1)));
        //group分组
        System.out.println(integerList.stream().collect(Collectors.groupingBy(k -> k)));
        //通过partitioningBy进行分区
        System.out.println(integerList.stream().collect(Collectors.partitioningBy(k -> k % 2 == 0)));
    }
}
