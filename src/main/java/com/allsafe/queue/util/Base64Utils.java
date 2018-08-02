package com.allsafe.queue.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {

  public static String encode(byte[] source) {
    return new String(Base64.encodeBase64(source));
  }

  public static byte[] decode(String source) {
    return Base64.decodeBase64(source.getBytes());
  }
}