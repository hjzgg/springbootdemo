package com.hjzgg.example.springboot.stream;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author hujunzheng
 * @create 2020-06-19 17:56
 **/
public class StreamCombineAndPartition {

    public static void main(String[] args) {
        List<Response> responses = IntStream.range(0, 100)
                .mapToObj(i -> Response.builder()
                        .add(
                                IntStream.range(0, 20)
                                        .mapToObj(j -> "add_" + (i + j))
                                        .collect(Collectors.toList())
                        )
                        .update(
                                IntStream.range(0, 20)
                                        .mapToObj(j -> "update_" + (i + j))
                                        .collect(Collectors.toList())
                        )
                        .build()
                )
                .collect(Collectors.toList());
        process1(responses);
        process2(responses);
    }

    private static void process1(List<Response> responses) {
        Map<Boolean, List<List<String>>> combineAndPartitionResponse = responses.stream()
                .map(response -> Stream.of
                        (
                                new Pair<>(Boolean.TRUE, response.getUpdate())
                                , new Pair<>(Boolean.FALSE, response.getAdd())
                        )
                )
                .flatMap(Function.identity())
                .collect(Collectors.partitioningBy(
                        Pair::getKey
                        , Collectors.mapping(
                                Pair::getValue
                                , Collectors.toList()
                        )
                ));

        List<String> combineAddResponse = combineAndPartitionResponse.containsKey(Boolean.FALSE) ?
                combineAndPartitionResponse.get(Boolean.FALSE).stream().flatMap(List::stream).collect(Collectors.toList()) :
                Collections.emptyList();

        List<String> combineUpdateResponse = combineAndPartitionResponse.containsKey(Boolean.TRUE) ?
                combineAndPartitionResponse.get(Boolean.TRUE).stream().flatMap(List::stream).collect(Collectors.toList()) :
                Collections.emptyList();

        System.out.println("combineAddResponse: " + combineAddResponse);
        System.out.println("combineUpdateResponse: " + combineUpdateResponse);
    }


    private static void process2(List<Response> responses) {
        Map<Boolean, List<String>> combineAndPartitionResponse = responses.stream()
                .map(response -> Stream.of
                        (
                                response.getUpdate()
                                        .stream()
                                        .map(userId -> new Pair<>(Boolean.TRUE, userId))
                                , response.getAdd()
                                        .stream()
                                        .map(userId -> new Pair<>((Boolean.FALSE), userId))
                        )
                        .flatMap(Function.identity())
                )
                .flatMap(Function.identity())
                .collect(Collectors.partitioningBy(
                        Pair::getKey
                        , Collectors.mapping(
                                Pair::getValue
                                , Collectors.toList()
                        )
                ));

        List<String> combineAddResponse = combineAndPartitionResponse.containsKey(Boolean.FALSE) ?
                combineAndPartitionResponse.get(Boolean.FALSE) :
                Collections.emptyList();

        List<String> combineUpdateResponse = combineAndPartitionResponse.containsKey(Boolean.TRUE) ?
                combineAndPartitionResponse.get(Boolean.TRUE) :
                Collections.emptyList();

        System.out.println("combineAddResponse: " + combineAddResponse);
        System.out.println("combineUpdateResponse: " + combineUpdateResponse);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Response {
        private List<String> add;
        private List<String> update;
    }
}