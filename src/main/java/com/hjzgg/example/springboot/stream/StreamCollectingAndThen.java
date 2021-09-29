package com.hjzgg.example.springboot.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author hujunzheng
 * @create 2021-09-29 10:51
 **/
public class StreamCollectingAndThen {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(5);
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(2);

        list = list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toSet(),
                        /** 将Set结果传给ArrayList构造器{@link ArrayList#ArrayList(java.util.Collection)} */
                        ArrayList::new
                ));

        //去重之后的list
        System.out.println(list);

        //等价于 Supplier<MyCollection> emptyMyCollectionSupplier = () -> new MyCollection();
        Supplier<MyCollection> emptyMyCollectionSupplier = MyCollection::new;

        //等价于 Function<List<Integer>, MyCollection> myCollectionFunction = l -> new MyCollection(l);
        Function<List<Integer>, MyCollection> myCollectionFunction = MyCollection::new;
    }

    static class MyCollection {
        private List<Integer> list;

        public MyCollection() {
            this.list = Collections.emptyList();
        }

        public MyCollection(List<Integer> list) {
            this.list = list;
        }

        public List<Integer> getList() {
            return list;
        }
    }
}