   本人负责的模块内对延迟要求较高在使用缓存之外、依赖该工具用于模块的异步操作、后续还会补充Process的处理器部分的实现

`note:如果Coding中有问题，还请大家及时多多指正`


#### EXAMPLE

* Business Facade Demonstrate:
``` java
@Service
public class KeyworkResourceFacdae {

  @Resource
  private QueueService queueService

  // 此处进行业务层的调用函数的声明
  public void saveOrUpdateXXX(CourseKeywordResourceModel ckrm) {
    CourseKeywordResourceModel ckrm = null;
    
    // 具体的业务处理逻辑 start
    ckrm = init()
    // 具体的业务处理逻辑 end
    
    // 需要异步处理
    queueService.addKeyworkResourceAndUpdateCourseResource(Message.AddKeyworkResourceAndUpdateCourseResource.toString(), ckrm);
  }

}
```

* 生产者:
``` java
@Service("queueService")
public class QueueService {

  // 生命消息名
  private enum Message {
    AddKeyworkResourceAndUpdateCourseResource,
    ...  // 去定义自己的消息名称
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
  
  .... // 去定义自己需要的异步方法

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

#### Advanced paragraphs
##### 1. 需要处理的实体时间有依赖性要求(即数据处理的时序行)<br>
比如：<br>
 Course-->tb_course、Chapter-->tb_chapter、Item-->tb_item <br>
tb_item表内含有tb_chapter的外键、而tb_chapter表内含有tb_course的外键<br>
即数据落库顺序应该为tb_course->tb_chapter->tb_item，这样方可保存各自父级的引用用于保存自己当前的数据<br>


// 声明Service
``` java
@Service("queueService")
public class QueueService {

  public enum Message {
    CreateCourse, CreateChapter, CreateItem, CreateResource
  }

  private IMQClient m = new AbstractMQClient(AliyunWorker.class, DefaultMessageQueueManager.class);

  public void sendMessageBox(MessageBox box) {
    m.sendMessage(box);
  }
}
```
// 业务方法中会通过用户定义的时序前后queue会顺序执行添加到队列的消息
``` java
public void createCourseInfo(CourseInfoPojo pojo) {
   if (null == pojo) return;
    MessageBox box = new MessageBox();

    MoocCourse course = new MoocCourse();
    // 创建Course的优先级为最高
    box.addCurrentLevelMessage(Message.CreateCourse.toString(), course);
    if (null == pojo.getChapterArray() || pojo.getChapterArray().isEmpty()) return;
    for (ChapterInfo chapterInfo : pojo.getChapterArray()) {
        MoocChapter chapter = new MoocChapter();

        // 创建Chapter的优先级为其次
        box.addCurrentLevelMessage(Message.CreateChapter.toString(), chapter);
        if (null != chapterInfo.getItemArray() && !chapterInfo.getItemArray().isEmpty()) {
            for (ItemInfo itemInfo : chapterInfo.getItemArray()) {
                MoocItem item = new MoocItem();
        
                item.setChapter(chapter);
		
	       // 创建Chapter的优先级为最后
               box.addCurrentLevelMessage(Message.CreateItem.toString(), item);
            }
        }
     }
     queueService.sendMessageBox(box);
}
```
完！


##### 2. 消息拒绝策略(RejectedExecutionHandler)
	// TODO
