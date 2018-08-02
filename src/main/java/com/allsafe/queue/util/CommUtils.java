/**
 * 
 */
package com.allsafe.queue.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @name CommUtils 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description TODO
 * @version 1.0
 */
public class CommUtils {
  /**
   * 获取服务器IP
   * 
   * @return 服务器IP
   */
  public static String getServerIp() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostAddress().toString();// 获得本机IP
    } catch (UnknownHostException e) {
      LoggerUtil.error("Err:Unknown Host.", e);
      return null;
    }
  }

  /**
   * 获取服务器名
   * 
   * @return 服务器名
   */
  public static String getServerName() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostName().toString();// 获得机器名
    } catch (UnknownHostException e) {
      LoggerUtil.error("Err:Unknown Host.", e);
      return "";
    }
  }
}
