package com.hjzgg.example.springboot.beans.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hjzgg.example.springboot.utils.JacksonHelper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * */
public enum EnumType {
    TEST_ENUM_TYPE("test", "测试");

    private String code;
    private String label;

    EnumType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private static Map<String, EnumType> map = new HashMap<>();

    static {
        EnumSet.allOf(EnumType.class).forEach(enumType -> map.put(enumType.getCode(), enumType));
    }

    @JsonCreator
    public static EnumType getBizType(String code) {
        return map.get(code);
    }

    @JsonValue
    public String getValue() {
        return this.code;
    }

    public static class DataVO {
        private EnumType enumType = EnumType.TEST_ENUM_TYPE;

        public EnumType getEnumType() {
            return enumType;
        }

        public void setEnumType(EnumType enumType) {
            this.enumType = enumType;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(JacksonHelper.getObjectMapper().writeValueAsString(new DataVO()));
    }
}