package com.allsafe.queue.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;


/**
 * @name StringUtils 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 字符串操作类
 * @version 1.0
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
  public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
  public static final String GROUP_KEY = "group";
  public static final String INTERFACE_KEY = "interface";
  public static final String VERSION_KEY = "version";
  public static final String COMMA_SEPARATOR = ",";
  public static final String POINT_SEPRATOR = ".";


  /** 空字符串 */
  private final static String NULL = "";

  public static final String[] EMPTY_STRING_ARRAY = new String[0];

  private static final Pattern KVP_PATTERN = Pattern
      .compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)"); // key value pair pattern.

  private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

  private final static String JSON_SEP = "[\\{\\}\\[\\]]";

  /**
   * 获取一个有效的字符串 屏蔽返回NULL造成的空指针异常
   * 
   * @param value 源字符串
   * @return 有效字符串
   */
  public static String effectVal(String value) {
    return isNotEmpty(value) ? value : NULL;
  }

  /**
   * 获取一个有效的字符串 屏蔽返回NULL造成的空指针异常
   * 
   * @param value 源字符串
   * @return 有效字符串
   */
  public static String effectJsonVal(String value) {
    return isJsonNotBlank(value) ? value : NULL;
  }

  /**
   * 判断json串是否为空
   * 
   * @param jsonStr 目标串
   * @return 结果
   */
  public static boolean isJsonBlank(String jsonStr) {
    int matchLen = 0;
    if (isBlank(jsonStr)) return true;
    Matcher matcher = Pattern.compile(JSON_SEP).matcher(jsonStr);
    while (matcher.find()) {
      matchLen += matcher.end() - matcher.start();
    }
    return jsonStr.length() == matchLen;
  }

  /**
   * 判断json串是否非空
   * 
   * @param jsonStr 目标串
   * @return 结果
   */
  public static boolean isJsonNotBlank(String jsonStr) {
    return !isJsonBlank(jsonStr);
  }

  /**
   * 判断一组字符串是否都不为空
   * 
   * @param param 字符串数组
   * @return 结果
   */
  public static boolean isAllNotBlank(String... param) {
    boolean _isNblank = true;
    if (param.length == 1) return isNotBlank(param[0]);
    for (String _param : param) {
      _isNblank = _isNblank && isNotBlank(_param);
    }
    return _isNblank;
  }

  /**
   * 判断一组字符串是否有不为空的字符串
   * 
   * @param param 字符串数组
   * @return 结果
   */
  public static boolean isOrNotBlank(String... param) {
    boolean _isNblank = false;
    if (param.length == 1) return isNotBlank(param[0]);
    for (String _param : param) {
      _isNblank = _isNblank || isNotBlank(_param);
    }
    return _isNblank;
  }

  /**
   * 判断一组字符串是否都为空
   * 
   * @param param 字符串数组
   * @return 结果
   */
  public static boolean isAllBlank(String... param) {
    boolean _isblank = true;
    if (param.length == 1) return isBlank(param[0]);
    for (String _param : param) {
      _isblank = _isblank && isBlank(_param);
    }
    return _isblank;
  }

  /**
   * 判断一组字符串是否有为空的字符串
   * 
   * @param param 字符串数组
   * @return 结果
   */
  public static boolean isOrBlank(String... param) {
    boolean _isblank = false;
    if (param.length == 1) return isBlank(param[0]);
    for (String _param : param) {
      _isblank = _isblank || isBlank(_param);
    }
    return _isblank;
  }

  public static boolean isBlank(String str) {
    if (str == null || str.length() == 0) return true;
    return false;
  }

  /**
   * is empty string.
   * 
   * @param str source string.
   * @return is empty.
   */
  public static boolean isEmpty(String str) {
    if (str == null || str.length() == 0) return true;
    return false;
  }

  /**
   * is not empty string.
   * 
   * @param str source string.
   * @return is not empty.
   */
  public static boolean isNotEmpty(String str) {
    return str != null && str.length() > 0;
  }

  /**
   * 
   * @param s1
   * @param s2
   * @return equals
   */
  public static boolean isEquals(String s1, String s2) {
    if (s1 == null && s2 == null) return true;
    if (s1 == null || s2 == null) return false;
    return s1.equals(s2);
  }

  /**
   * is integer string.
   * 
   * @param str
   * @return is integer
   */
  public static boolean isInteger(String str) {
    if (str == null || str.length() == 0) return false;
    return INT_PATTERN.matcher(str).matches();
  }

  public static int parseInteger(String str) {
    if (!isInteger(str)) return 0;
    return Integer.parseInt(str);
  }

  /**
   * Returns true if s is a legal Java identifier.
   * <p>
   * <a href="http://www.exampledepot.com/egs/java.lang/IsJavaId.html">more info.</a>
   */
  public static boolean isJavaIdentifier(String s) {
    if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
      return false;
    }
    for (int i = 1; i < s.length(); i++) {
      if (!Character.isJavaIdentifierPart(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static boolean isContains(String values, String value) {
    if (values == null || values.length() == 0) {
      return false;
    }
    return isContains(COMMA_SPLIT_PATTERN.split(values), value);
  }

  /**
   * 
   * @param values
   * @param value
   * @return contains
   */
  public static boolean isContains(String[] values, String value) {
    if (value != null && value.length() > 0 && values != null && values.length > 0) {
      for (String v : values) {
        if (value.equals(v)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isNumeric(String str) {
    if (str == null) {
      return false;
    }
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
      if (Character.isDigit(str.charAt(i)) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * 
   * @param e
   * @return string
   */
  public static String toString(Throwable e) {
    UnsafeStringWriter w = new UnsafeStringWriter();
    PrintWriter p = new PrintWriter(w);
    p.print(e.getClass().getName());
    if (e.getMessage() != null) {
      p.print(": " + e.getMessage());
    }
    p.println();
    try {
      e.printStackTrace(p);
      return w.toString();
    } finally {
      p.close();
    }
  }

  /**
   * 
   * @param msg
   * @param e
   * @return string
   */
  public static String toString(String msg, Throwable e) {
    UnsafeStringWriter w = new UnsafeStringWriter();
    w.write(msg + "\n");
    PrintWriter p = new PrintWriter(w);
    try {
      e.printStackTrace(p);
      return w.toString();
    } finally {
      p.close();
    }
  }

  /**
   * translat.
   * 
   * @param src source string.
   * @param from src char table.
   * @param to target char table.
   * @return String.
   */
  public static String translat(String src, String from, String to) {
    if (isEmpty(src)) return src;
    StringBuilder sb = null;
    int ix;
    char c;
    for (int i = 0, len = src.length(); i < len; i++) {
      c = src.charAt(i);
      ix = from.indexOf(c);
      if (ix == -1) {
        if (sb != null) sb.append(c);
      } else {
        if (sb == null) {
          sb = new StringBuilder(len);
          sb.append(src, 0, i);
        }
        if (ix < to.length()) sb.append(to.charAt(ix));
      }
    }
    return sb == null ? src : sb.toString();
  }

  /**
   * split.
   * 
   * @param ch char.
   * @return string array.
   */
  public static String[] split(String str, char ch) {
    List<String> list = null;
    char c;
    int ix = 0, len = str.length();
    for (int i = 0; i < len; i++) {
      c = str.charAt(i);
      if (c == ch) {
        if (list == null) list = new ArrayList<String>();
        list.add(str.substring(ix, i));
        ix = i + 1;
      }
    }
    if (ix > 0) list.add(str.substring(ix));
    return list == null ? EMPTY_STRING_ARRAY : (String[]) list.toArray(EMPTY_STRING_ARRAY);
  }

  /**
   * join string.
   * 
   * @param array String array.
   * @return String.
   */
  public static String join(String[] array) {
    if (array.length == 0) return "";
    StringBuilder sb = new StringBuilder();
    for (String s : array)
      sb.append(s);
    return sb.toString();
  }

  /**
   * join string like javascript.
   * 
   * @param array String array.
   * @param split split
   * @return String.
   */
  public static String join(String[] array, char split) {
    if (array.length == 0) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      if (i > 0) sb.append(split);
      sb.append(array[i]);
    }
    return sb.toString();
  }

  /**
   * join string like javascript.
   * 
   * @param array String array.
   * @param split split
   * @return String.
   */
  public static String join(String[] array, String split) {
    if (array.length == 0) return "";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      if (i > 0) sb.append(split);
      sb.append(array[i]);
    }
    return sb.toString();
  }

  /**
   * parse key-value pair.
   * 
   * @param str string.
   * @param itemSeparator item separator.
   * @return key-value map;
   */
  private static Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
    String[] tmp = str.split(itemSeparator);
    Map<String, String> map = new HashMap<String, String>(tmp.length);
    for (int i = 0; i < tmp.length; i++) {
      Matcher matcher = KVP_PATTERN.matcher(tmp[i]);
      if (matcher.matches() == false) continue;
      map.put(matcher.group(1), matcher.group(2));
    }
    return map;
  }

  public static String getQueryStringValue(String qs, String key) {
    Map<String, String> map = StringUtils.parseQueryString(qs);
    return map.get(key);
  }

  /**
   * parse query string to Parameters.
   * 
   * @param qs query string.
   * @return Parameters instance.
   */
  public static Map<String, String> parseQueryString(String qs) {
    if (qs == null || qs.length() == 0) return new HashMap<String, String>();
    return parseKeyValuePair(qs, "\\&");
  }

  public static String getServiceKey(Map<String, String> ps) {
    StringBuilder buf = new StringBuilder();
    String group = ps.get(GROUP_KEY);
    if (group != null && group.length() > 0) {
      buf.append(group).append("/");
    }
    buf.append(ps.get(INTERFACE_KEY));
    String version = ps.get(VERSION_KEY);
    if (version != null && version.length() > 0) {
      buf.append(":").append(version);
    }
    return buf.toString();
  }

  public static String toQueryString(Map<String, String> ps) {
    StringBuilder buf = new StringBuilder();
    if (ps != null && ps.size() > 0) {
      for (Map.Entry<String, String> entry : new TreeMap<String, String>(ps).entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (key != null && key.length() > 0 && value != null && value.length() > 0) {
          if (buf.length() > 0) {
            buf.append("&");
          }
          buf.append(key);
          buf.append("=");
          buf.append(value);
        }
      }
    }
    return buf.toString();
  }

  public static String camelToSplitName(String camelName, String split) {
    if (camelName == null || camelName.length() == 0) {
      return camelName;
    }
    StringBuilder buf = null;
    for (int i = 0; i < camelName.length(); i++) {
      char ch = camelName.charAt(i);
      if (ch >= 'A' && ch <= 'Z') {
        if (buf == null) {
          buf = new StringBuilder();
          if (i > 0) {
            buf.append(camelName.substring(0, i));
          }
        }
        if (i > 0) {
          buf.append(split);
        }
        buf.append(Character.toLowerCase(ch));
      } else if (buf != null) {
        buf.append(ch);
      }
    }
    return buf == null ? camelName : buf.toString();
  }

  public static String toArgumentString(Object[] args) {
    StringBuilder buf = new StringBuilder();
    for (Object arg : args) {
      if (buf.length() > 0) {
        buf.append(COMMA_SEPARATOR);
      }
      if (arg == null || ReflectUtils.isPrimitives(arg.getClass())) {
        buf.append(arg);
      } else {
        buf.append(JSON.toJSON(arg));
      }
    }
    return buf.toString();
  }
}
