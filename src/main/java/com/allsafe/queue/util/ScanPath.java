package com.allsafe.queue.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @name ScanPath 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 浏览指定路径下的所有类,并对其做处理
 * @version 1.0
 */
public abstract class ScanPath {

  /** recursive 标记循环浏览 */
  private boolean recursive = true;
  /** packageName 扫描路径 */
  private String packageName;
  /** packageDirName 包名 */
  private String packageDirName;

  /**
   * 从包package中获取所有的Class
   * 
   * @param scanPath
   */
  public ScanPath(String scanPath) {
    this(scanPath, true);
  }

  /**
   * 从包package中获取所有的Class,指定是否循环package中的目录
   * 
   * @param scanPath
   * @param recursive
   */
  public ScanPath(String scanPath, boolean recursive) {
    if (StringUtils.isBlank(scanPath)) return;
    // 是否循环迭代
    this.recursive = recursive;
    // 获取包的名字 并进行替换
    this.packageName = scanPath;
    this.packageDirName = scanPath.replace('.', '/');
    // 定义一个枚举的集合 并进行循环来处理这个目录下的things
    Enumeration<URL> dirs;
    try {
      dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
      // 循环迭代下去
      while (dirs.hasMoreElements()) {
        // 获取下一个元素
        URL url = dirs.nextElement();
        // 得到协议的名称
        String protocol = url.getProtocol();
        // 如果是以文件的形式保存在服务器上
        if ("file".equals(protocol)) {
          // 获取包的物理路径
          String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
          // 以文件的方式扫描整个包下的文件 并添加到集合中
          findAndAddClassesInPackageByFile(packageName, filePath, recursive);
        } else if ("jar".equals(protocol)) {
          // 如果是jar包文件
          processJarFile(url);
        }
      }
    } catch (IOException e) {
      LoggerUtil.error("在扫描用户定义视图时获取文件出错", e);
    }
  }

  /**
   * 处理Jar包
   * 
   * @param url
   * @throws IOException
   */
  private void processJarFile(URL url) throws IOException {
    // 定义一个JarFile
    JarFile jar;
    // 获取jar
    jar = ((JarURLConnection) url.openConnection()).getJarFile();
    // 从此jar包 得到一个枚举类
    Enumeration<JarEntry> entries = jar.entries();
    // 同样的进行循环迭代
    while (entries.hasMoreElements()) {
      // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
      JarEntry entry = entries.nextElement();
      String name = entry.getName();
      // 如果是以/开头的
      if (name.charAt(0) == '/') name = name.substring(1); // 获取后面的字符串
      // 如果前半部分和定义的包名相同
      if (!name.startsWith(packageDirName)) continue;
      // 如果可以迭代下去 并且是一个包
      if (name.lastIndexOf('/') == -1 && !recursive) continue;
      // 如果是一个.class文件 而且不是目录
      if (name.endsWith(".class") && !entry.isDirectory()) {
        // 去掉后面的".class" 获取真正的类名
        try {
          // 添加到classes
          processClass(Thread.currentThread().getContextClassLoader()
              .loadClass(name.substring(0, name.length() - 6).replace('/', '.')));
        } catch (ClassNotFoundException e) {
          LoggerUtil.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
        }
      }
    }
  }

  /**
   * 以文件的形式来获取包下的所有Class
   * 
   * @param packageName
   * @param packagePath
   * @param recursive
   * @param classes
   */
  private void findAndAddClassesInPackageByFile(String packageName, String packagePath,
      final boolean recursive) {
    // 获取此包的目录 建立一个File
    File dir = new File(packagePath);
    // 如果不存在或者 也不是目录就直接返回
    if (!dir.exists() || !dir.isDirectory()) {
      LoggerUtil.error("用户定义包名 " + packageName + " 下没有任何文件", new RuntimeException());
      return;
    }
    // 如果存在 就获取包下的所有文件 包括目录
    File[] dirfiles = dir.listFiles(new FileFilter() {
      // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
      public boolean accept(File file) {
        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
      }
    });
    // 循环所有文件
    for (File file : dirfiles) {
      // 如果是目录 则继续扫描
      if (file.isDirectory()) {
        findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
            file.getAbsolutePath(), recursive);
      } else {
        // 如果是java类文件 去掉后面的.class 只留下类名
        String className = file.getName().substring(0, file.getName().length() - 6);
        try {
          // 添加到集合中去
          // 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
          processClass(Thread.currentThread().getContextClassLoader()
              .loadClass(packageName + '.' + className));;
        } catch (ClassNotFoundException e) {
          LoggerUtil.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
        }
      }
    }
  }

  /**
   * 实际处理方法
   * 
   * @param clazz
   */
  protected abstract void processClass(Class<?> clazz);

}