package com.tony.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class JsonUtils {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String obj2String(Object obj) {
    if (obj == null) {
      return null;
    }
    try {
      return obj instanceof String ? (String) obj
          : objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      return null;
    }
  }

  public static byte[] obj2Bytes(Object obj) {
    if (obj == null) {
      return null;
    }
    try {
      return obj instanceof byte[] ? (byte[]) obj
              : objectMapper.writeValueAsBytes(obj);
    } catch (Exception e) {
      return null;
    }
  }

  public static <T> T string2Obj(String str, Class<T> clazz) {
    if (str.trim().isEmpty() || clazz == null) {
      return null;
    }
    try {
      return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
    } catch (Exception e) {
      return null;
    }
  }

  public static <T> T bytes2Obj(byte[] bytes, Class<T> clazz) {
    if (bytes == null || clazz == null) {
      return null;
    }
    try {
      return objectMapper.readValue(bytes, clazz);
    } catch (Exception e) {
      return null;
    }
  }

  public static <T> T inputStream2Obj(InputStream is, Class<T> clazz) {
    if (is == null) {
      return null;
    }
    try {
      return clazz.equals(String.class) ? (T) null : objectMapper.readValue(is, clazz);
    } catch (Exception e) {
      return null;
    }
  }

}
