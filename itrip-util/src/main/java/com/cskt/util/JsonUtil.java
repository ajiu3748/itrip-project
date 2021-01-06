package com.cskt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @description:
 * @author: Mr.阿九
 * @createTime: 2021-01-06 13:52
 **/
public class JsonUtil {
    private static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
    }
    /**
     * 将Object对象转换成为Json字符串
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String objectToJsonString(Object object) throws
            JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
    /**
     * 将Json字符串反序列化为Object对象
     * @param jsonString 待反序列化的Json字符串
     * @param tClass 返回的类类型
     * @param <T> 泛型
     * @return
     * @throws JsonProcessingException
     */
    public static <T> T jsonStringToObject(String jsonString,Class<T> tClass)
            throws JsonProcessingException {
        return objectMapper.readValue(jsonString, tClass);
    }

}
