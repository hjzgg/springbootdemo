package com.hjzgg.example.springboot.test;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class JsonlibTest {
    public static void main(String[] args) throws IOException {
        String content = Files.lines(new File("D:/response.json").toPath()).collect(Collectors.joining(System.lineSeparator()));
        System.out.println(content);
        JSONObject jsonObject = JSONObject.fromObject(content);
        System.out.println(jsonObject);
    }
}
