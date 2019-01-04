package com.hjzgg.example.springboot.controller;

import com.hjzgg.example.springboot.utils.sign.Sign;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author hujunzheng
 * @create 2018-12-24 9:56
 **/
@RestController
@RequestMapping("sign")
public class SignController {

    @Sign
    @PostMapping(value = "test/{var1}/{var2}", produces = MediaType.ALL_VALUE)
    public String myController(@PathVariable String var1
            , @PathVariable String var2
            , @RequestParam String var3
            , @RequestParam String var4
            , @RequestBody User user) {
        return String.join(",", var1, var2, var3, var4, user.toString());
    }

    private static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("name", name)
                    .append("age", age)
                    .toString();
        }
    }
}