package com.allsafe.queue.config;

/**
 * @name AbstractMQServerConfig 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 定义MQ配置文件获取参数方法
 * @version 1.0
 */
public abstract class AbstractMQServerConfig implements IMQServerConfig {

  public final String getParam(ConfigParam param) {
    return _getParam0(param.toString());
  }

  protected abstract String _getParam0(String key);

}
