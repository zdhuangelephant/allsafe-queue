    本人负责的模块内对延迟要求较高在使用缓存之外、依赖该工具用于模块的异步操作、后续还会补充Process的处理器部分的实现

`note:如果Coding中有问题，还请大家及时多多指正`


EXAMPLE

* 生产者:
``` java
@Service("queueService")
public class QueueService {

  // 生命消息名
  private enum Message {
    AddKeyworkResourceAndUpdateCourseResource
  }

  // 定义队列的初始化数据、例如扫描模块指定的目录
  private ContainerParamModel queueContainerModel = new ContainerParamModel();
  {
    this.queueContainerModel.setScanPath("com.allsafe.ms");
  }
  
  // 初始化一个队列管理器
  private IMQClient m = new AbstractMQClient(null, queueContainerModel, DefaultWorker.class,
      DefaultMessageQueueManager.class);


  // 此处进行业务层的调用函数的声明
  public void addKeyworkResourceAndUpdateCourseResource(CourseKeywordResourceModel ckrm) {
    m.sendMessage(Message.AddKeyworkResourceAndUpdateCourseResource.toString(), ckrm);
  }

}
```



* 消费者:
``` java
@HandlerMessage(value = "AddKeyworkResourceAndUpdateCourseResource", 	type = MessageType.Single)
public class AddKeyworkResourceAndUpdateCourseResourceWork extends AbstractDefaultWorker {

  private static final long serialVersionUID = 681700950367469783L;

  @Override
  public void domain(DefaultMessage message, IMQCallBacker<DefaultMessage> callback) {
	// 从消息体内反序列化消息
    CourseKeywordResourceModel ckrm =
        FastJsonUtil.fromJson(message.getMessageBodyJson(), CourseKeywordResourceModel.class);
		
	// 从ApplicationContext内获取到某个Service的引用
    CourseKeywordResourceService courseKeywordResourceService =
        SpringWebContextHolder.getBean("courseKeywordResourceService");
	
	// 执行耗时的操作(写库、网络IO、或者CPU密集型计算等耗时较长的操作)
    courseKeywordResourceService.addKeywordResource(ckrm);
    courseKeywordResourceService.updateResourceKeyPoint(ckrm.getResourceId(),
        ckrm.getResourceType());
  }

  @Override
  public void domain(List<DefaultMessage> message, IMQCallBacker<List<DefaultMessage>> callback) {}

  @Override
  public void onException(Throwable t, List<DefaultMessage> message,
      IMQCallBacker<List<DefaultMessage>> callback) {}

  @Override
  public void onException(Throwable t, DefaultMessage message,
      IMQCallBacker<DefaultMessage> callback) {
    LoggerUtil.error("更新考点ERROR", t.getCause());
  }

}
```

* Worker空实现(Option)
``` java
public class DefaultWorker extends AbstractDefaultWorker{

  @Override
  public void domain(DefaultMessage message, IMQCallBacker<DefaultMessage> callback)
      throws Exception {
    
  }

  @Override
  public void domain(List<DefaultMessage> message, IMQCallBacker<List<DefaultMessage>> callback)
      throws Exception {
    
  }

  @Override
  public void onException(Throwable t, List<DefaultMessage> message,
      IMQCallBacker<List<DefaultMessage>> callback) {
    
  }

  @Override
  public void onException(Throwable t, DefaultMessage message,
      IMQCallBacker<DefaultMessage> callback) {
    
  }

}
```
