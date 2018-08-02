package com.allsafe.queue.config;


/**
 * @name IMQServerConfig 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description MQ服务端配置文件
 * @version 1.0
 */
public interface IMQServerConfig {
  public enum ConfigParam {
    /** ALIYUN_ACCESSKEYID */
    ALIYUN_ACCESSKEYID,
    /** ALIYUN_ACCESSKEYSECRET */
    ALIYUN_ACCESSKEYSECRET,
    /** ALIYUN_ACCOUNTENDPOINT */
    ALIYUN_ACCOUNTENDPOINT,
    /** ALIYUN_QUEUEMARKET */
    ALIYUN_QUEUEMARKET,
    /** ALIYUN_QUEUENUMBER */
    ALIYUN_QUEUENUMBER,
    /** ALIYUN_QUEUENAMEPREFIX 队列名称前缀 */
    ALIYUN_QUEUENAMEPREFIX,
    /** ALIYUN_QUEUEMESSAGEMAPPING 消息队列映射关系 */
    ALIYUN_QUEUEMESSAGEMAPPING,
    /** ALIYUN_QUEUESHORTNAMEMAPPING 消息队列简称映射关系 */
    ALIYUN_QUEUESHORTNAMEMAPPING,
    /** ALIYUN_BATCHLIMIT 批处理数量级 */
    ALIYUN_BATCHLIMIT
  }

  public String getParam(ConfigParam param);

}
