package com.hjzgg.example.springboot.utils;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hujunzheng
 * @create 2018-12-25 19:58
 **/
public class SignUtil {
    private static final String DEFAULT_SECRET = "1qaz@WSX#$%&";

    public static String sign(String body, Map<String, String[]> params, String[] paths) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(body)) {
            sb.append(body).append('#');
        }

        if (!CollectionUtils.isEmpty(params)) {
            params.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(paramEntry -> {
                        String paramValue = String.join(",", Arrays.stream(paramEntry.getValue()).sorted().toArray(String[]::new));
                        sb.append(paramEntry.getKey()).append("=").append(paramValue).append('#');
                    });
        }

        if (ArrayUtils.isNotEmpty(paths)) {
            String pathValues = String.join(",", Arrays.stream(paths).sorted().toArray(String[]::new));
            sb.append(pathValues);
        }

        String createSign = HmacUtils.hmacSha256Hex(DEFAULT_SECRET, sb.toString());
        return createSign;
    }

    public static void main(String[] args) {
        String body = "{\n" +
                "\t\"name\": \"hjzgg\",\n" +
                "\t\"age\": 26\n" +
                "}";
        Map<String, String[]> params = new HashMap<>();
        params.put("var3", new String[]{"3"});
        params.put("var4", new String[]{"4"});

        String[] paths = new String[]{"1", "2"};

        System.out.println(sign(body, params, paths));
    }

}