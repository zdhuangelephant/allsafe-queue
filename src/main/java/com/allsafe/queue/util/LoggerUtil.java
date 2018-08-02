package com.allsafe.queue.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @name LoggerUtil 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 日志记录
 * @version 1.0
 */
public class LoggerUtil {

  /** errorLogger */
  public static void error(String msg, Exception e) {
    printError(Logger.getLogger("errorLogger"), msg, e);
  }

  /**
   * errorLogger
   */
  public static void error(String msg, Throwable e) {
    printError(Logger.getLogger("errorLogger"), msg, e);
  }

  public static void printError(Logger logger, String msg, Throwable e) {
    if (null != logger && logger.isEnabledFor(Level.ERROR)) {
      if (msg != null) {
        logger.error(msg);
      }
      if (e != null) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(buf, true));
        logger.error(buf.toString());
      }
    }
  }
  
  /** 消息队列模块的Logger */
  public static void messageInfo(MessageEntity<?> entity) {
    info("messageLogger", entity);
  }

  private static void info(String logger, Object msg) {
    if (null != logger && Logger.getLogger(logger).isInfoEnabled()) {
      Logger.getLogger(logger).info(msg);
    }
  }

}
