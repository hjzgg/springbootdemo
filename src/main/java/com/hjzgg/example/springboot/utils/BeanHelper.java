package com.hjzgg.example.springboot.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author hujunzheng
 * @create 2020-08-05 14:41
 **/
public class BeanHelper {
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        return Arrays.stream(pds)
                .filter(pd -> Objects.isNull(beanWrapper.getPropertyValue(pd.getName())))
                .map(PropertyDescriptor::getName)
                .distinct()
                .toArray(String[]::new);
    }

    public static void main(String[] args) {
        TestBean testBean1 = new TestBean();
        testBean1.setA(1);
        testBean1.setB("2");
        System.out.println(testBean1);

        TestBean testBean2 = new TestBean();
        testBean2.setA(2);
        BeanUtils.copyProperties(testBean2, testBean1, BeanHelper.getNullPropertyNames(testBean2));
        System.out.println(testBean1);

    }

    private static class TestBean {
        private Integer a;
        private String b;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("a", a)
                    .append("b", b)
                    .toString();
        }
    }
}