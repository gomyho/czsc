package vip.qianbai.czsc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanchun on 2/17/16.
 */
public class JsonUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  private static ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static String toJson(Object obj) {
    try {
      if(obj != null) {
        return mapper.writeValueAsString(obj);
      }
    } catch (Exception e) {
      logger.debug("convert to json error for object: {}", obj, e);
    }
    return null;
  }

  public static void toFile(Object obj,String filePath){
    try {
      mapper.writeValue(new FileOutputStream(new File(filePath)), obj);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public static <T> T fromJson(String json, TypeReference<T> type) {
    
    if (json == null) {
      return null;
    }
    try {
      return mapper.readValue(json, type);
    } catch (Exception e) {
      logger.info("Cannot parse json string to Object. Json: <"
          + json + ">, Object class: <" + type.toString() + ">.", e);
    }
    return null;
  }
  public static <T> T fromJson(String json, Class<T> clazz) {

    if (json == null) {
      return null;
    }
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      logger.info("Cannot parse json string to Object. Json: <"
          + json + ">, Object class: <" + clazz.getName() + ">.", e);
    }
    return null;
  }
}
