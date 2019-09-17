package com.revolut.tx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);

    private JsonUtil() {

    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper;
    }

    public static String getPrettyJson(Object object) {

        ObjectMapper mapper = getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
        }
        return json;
    }

    public static String getJson(Object object) {
        ObjectMapper mapper = getMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Failed to convert Object to String.", e);
        }
        return json;
    }

    public static <T> T getObject(String jsonString, Class<T> clazz) throws IOException {
        ObjectMapper mapper = getMapper();

        T t = mapper.readValue(jsonString, clazz);
        return t;
    }

    public static <T> T getObject(String jsonString, Class<T> clazz, String root) throws IOException {
        if (StringUtils.isBlank(root)) {
            return getObject(jsonString, clazz);
        }
        ObjectMapper mapper = getMapper();
        JsonNode jsonNode = mapper.readTree(jsonString).findValue(root);
        T t = mapper.readValue(jsonNode.asText(), clazz);
        return t;
    }

    public static <T> List<T> getList(String jsonString, Class<T> clazz, String root) throws IOException {
        if (StringUtils.isBlank(root)) {
            return getList(jsonString, clazz);
        }
        ObjectMapper mapper = getMapper();
        JsonNode jsonNode = mapper.readTree(jsonString).findValue(root);
        List<T> list = new ArrayList<T>();
        if (jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.iterator();
            while (it.hasNext()) {
                T t = mapper.readValue(it.next().asText(), clazz);
                list.add(t);
            }
        }
        return list;
    }

    public static <T> List<T> getList(String jsonString, Class<T> clazz) throws IOException {
        ObjectMapper mapper = getMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        List<T> list = new ArrayList<T>();
        if (jsonNode.isArray()) {
            Iterator<JsonNode> it = jsonNode.elements();
            while (it.hasNext()) {
                T t = mapper.readValue(it.next().asText(), clazz);
                list.add(t);
            }
        }
        return list;
    }

    public static <T> T getList(String jsonString, TypeReference<T> reference) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, reference);
    }
    
    public static String getJsonWithMixIns(Object object, Map<Class<?>, Class<?>> mixinAnnotationsMap) {
        ObjectMapper mapper = getMapper();
        String json = null;
        try {
            mixinAnnotationsMap.forEach((target,mixinSource)-> {
                mapper.addMixInAnnotations(target, mixinSource);
            });
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("Failed to convert Object to String.", e);
        }
        return json;
    }

    public static <T> Map<String, T> getMap(String jsonString, Class<T> Clazz) throws IOException {
        ObjectMapper mapper = getMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Clazz);
        return mapper.readValue(jsonString, mapType);
    }
}
