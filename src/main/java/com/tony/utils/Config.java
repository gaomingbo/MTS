package com.tony.utils;

import java.io.*;
import java.util.*;

public class Config {

  public static Map<String, String> conf;

  static {
    try {
      Config config = new Config();
      conf = config.readAllProperties();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //配置文件的路径
  private String configPath = null;

  /**
   * 配置文件对象
   */
  private Properties props = null;

  /**
   * 默认构造函数，用于sh运行，自动找到classpath下的config.properties。
   */
  public Config() throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader("conf/config.properties"));
    props = new Properties();
    props.load(bufferedReader);
    //关闭资源
    bufferedReader.close();
  }

  /**
   * 根据key值读取配置的值
   * Jun 26, 2010 9:15:43 PM
   *
   * @param key key值
   * @return key 键对应的值
   * @throws IOException
   * @author GaoYu
   */
  public String readValue(String key) throws IOException {
    return props.getProperty(key);
  }

  /**
   * 读取properties的全部信息
   * Jun 26, 2010 9:21:01 PM
   *
   * @throws FileNotFoundException 配置文件没有找到
   * @throws IOException           关闭资源文件，或者加载配置文件错误
   * @author GaoYu
   */
  public Map<String, String> readAllProperties() throws FileNotFoundException, IOException {
    //保存所有的键值
    Map<String, String> map = new HashMap<String, String>();
    Enumeration en = props.propertyNames();
    while (en.hasMoreElements()) {
      String key = (String) en.nextElement();
      String Property = props.getProperty(key);
      map.put(key, Property);
    }
    return map;
  }

  /**
   * 设置某个key的值,并保存至文件。
   * Jun 26, 2010 9:15:43 PM
   *
   * @param key key值
   * @return key 键对应的值
   * @throws IOException
   * @author GaoYu
   */
  public void setValue(String key, String value) throws IOException {
    Properties prop = new Properties();
    InputStream fis = new FileInputStream(this.configPath);
    // 从输入流中读取属性列表（键和元素对）
    prop.load(fis);
    // 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
    // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
    OutputStream fos = new FileOutputStream(this.configPath);
    prop.setProperty(key, value);
    // 以适合使用 load 方法加载到 Properties 表中的格式，
    // 将此 Properties 表中的属性列表（键和元素对）写入输出流
    prop.store(fos, "last update");
    //关闭文件
    fis.close();
    fos.close();
  }

}
