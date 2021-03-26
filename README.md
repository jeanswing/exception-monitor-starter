#Exception-Monitor(1.0.0-release初版)

   * 前言: 这是菜鸟本人第一次在git上提交的开源项目，产品是类似于一个轻量"中间件"(吹个牛各位大佬轻喷)，模仿常用的中间件模式，致力于达到即插即用、低资源消耗、稳定可靠。
项目起始于公司实时监控看板(大屏显示)的需要，用来监控和统计大量接口的异常(已经使用于两个系统生产环境)，目前开源的是
Client使用端代码，Server端涉及到公司定制化不便开源，但不影响使用，只需自主实现及少量特定接口即可。
项目全程由个人开发，中间经历了很多开发中难以遇到的
问题，如打包差异、依赖冲突、环境兼容、可能一辈子只会见到一次的源码报错等，也算花费了心血，
项目代码量不算多，开发的也是急急忙忙，但过程中一直在思考一些问题，如
如何设计让用户最大程度方便使用？怎么最大限度的降低系统资源消耗？怎么同时用于Spring，SpringBoot，微服务SpringCloud等不同场景？
希望看官大佬们给予指正，共同交流学习，我个人也会空余维护这个项目，就当学习，希望借此为起点学习github更多优秀开源，开发更优秀的代码，谢谢。

   
## 1、功能介绍: 
   * exception-monitor-starter用于在基于java开发的系统中采集异常信息数据，支持单体和集群微服务，支持任何类型配置中心的动态刷新，不支持持久化，主要用于对异常的统计和实时通知。
   * 模式介绍:
   
     中心模式: 配置server-address即为启用，会持续保持与S端心跳测试，以队列方式向S端产生异常数据，S端定制收集和通知功能，基于DB
     持久化，保留了一些扩展接口。<br/>
     单机模式: 不配置server-address即为启用，需要用户自己实现IExceptionNotify接口的收集和通知方法，基于队列实现。
     
   * 采集异常信息包括: 异常所在类类型、异常类型、异常详情、代码文件位置、方法名称、代码行数，异常发生时间和自定义信息等内容。
    
## 2、原理介绍:
   * 2.1、基于生产者消费者模式实现，C端使用多线程生产捕捉到的异常任务，以java进程为单位，任何一个java进程持有默认30000(可配置)的任务队列: 
      * 2.1.1、当配置enable为false时，表示不启用exception-monitor-starter，系统不会初始化所有相关资源；当启用但未配置server-address时，
        系统使用IExceptionNotify发送和收集通知，不启用部分线程资源。
      * 2.1.2、消费线程使用懒加载模式，当未捕获到任何异常时，不会启动消费线程资源。
      * 2.1.3、当任务队列满员时，守护线程启动熔断措施，自动停止生产者线程，从而停止异常任务生产；当任务队列有开始消费时，保护线程自动开启生
        产者任务进行生产。
      * 2.1.4、使用monitor中心模式时，C端默认与monitor端保持心跳测试，当心跳正常时才会尝试发送任务，发送失败的任务，会默认进行三次重试。
   * 2.2、通知限流，用户自定义配置通知限制规则:<br/>
      * 2.2.1、total  基于总通知量限流，同一个通知Id(如邮箱)的总量达到阈值，24h后才会接收新的通知。<br/>
      * 2.2.2、exception  基于异常类型限流，同一个通知Id的同一个异常类型达到阈值，24h后才会接收新的通知。<br/>
      * 2.2.3、method  基于方法类型限流，同一个通知Id的相同方法所产生异常达到阈值，24h才会接收新的通知。<br/>
      * 2.2.4、class  基于类类型限流，同一个通知Id的相同类中所产生异常达到阈值，24h才会接收新的通知。<br/>
   * 2.3、动态配置，考虑到不同应用场景和使用不同的配置中心中间件，系统提供了接口IMonitorEventListener和基础实现，用户也可以自定义实现，用户只需在配置中心的Change事件调用接口方法即可。
      * 2.3.1、用户注入ConfigRefreshListener，调用eventPublish，即只执行配置刷新功能。
      * 2.3.1、用户注入ConfigRefreshListener，调用configRefreshEventRegisterPublishAll，即执行所有注册实现。
## 3、使用核心注解: @ExceptionMonitor
   * 注解提供继承性，建议不要使用在baseService,baseController等基础base类上，建议放在具体接口service、方法、baseMapper上。
   * 注解参数: <br/>
     expandExcDetail    是否展开所有异常信息,默认true。<br/>
     ignoreExc          忽略的异常名,支持类名简写和全写,忽略大小写。<br/>
     custom             定制化信息,便于区分重要性和标识特殊性。<br/>
     notifyUsers        通知到的用户唯一标识(如邮件、企业微信等)，优先级高于全局系统配置(不配置默认使用全局)。<br/>
           
## 4、使用示例:
   * java:
   ````
   @GetMapping("/order/detail/{id}")
   @ExceptionMonitor(ignoreExc = {"classnotfoundexception","serviceException","java.lang.IllegalArgumentException"},
               custom = "订单详情接口",expandExcDetail = false,notifyUsers = {"Alibaba.Jack@qq.com,David.Jack@tencent.com"})
   public Object list(@PathVariable("id")String id){
       int i =1/0;
       return i;
   }
   ````
   
   * java:
   ````
   @RestController
   @RequestMapping(value = "/order",produces = {"application/json;charset=UTF-8"})
   @ExceptionMonitor(ignoreExc = {"classnotfoundexception","serviceException","java.lang.IllegalArgumentException"},
                  custom = "订单处理Controller",expandExcDetail = false,notifyUsers = {"Alibaba.Jack@qq.com,David.Jack@tencent.com"})
   public class OrderController {
      
        @GetMapping("/detail/{id}")
        public Object list(@PathVariable("id")String id){
             int i =1/0;
             return i;
        }
   }
   ````
   
   * 配置文件application-prod.yml:
   ````
    monitor:
        #是否开启异常捕获内容
        enable: true
        #当前系统名称(用于提示区分不同系统)
        system-name: scm
        #监控中心服务地址
        server-address: http://172.18.253.4:6699
        #通知的用户唯一标识(多个使用,隔开)
        receive-user: wuying.ma@qq.com,Java.ma@qq.com,xxxx-userId
        #最大任务数，默认30000(当monitor端网络问题无法连接时，避免任务队列内存OOM，进行限制)
        max-queue-size: 20000
        flow:
            #是否开启通知限流
            enable: true
            #限流规则类型
            ruleType: total
            #限流通知数量
            num: 200
   ```` 
   