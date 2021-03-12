#Exception-Monitor(1.0.0-release初版)

## 1、功能介绍: 
   * exception-monitor-starter用于在基于java开发的系统中捕捉异常信息数据(支持集群和微服务，不支持持久化)，用于统计和实时通知。
   * 模式介绍:
   
     中心模式: 配置server-address即为启用，保持与S端心跳测试，以队列方式向S端产生异常数据，S端有基础收集和通知功能，基于database<br/>
     持久化，并保留扩展接口。<br/>
     单机模式: 不配置server-address即为启用，需要用户自己实现IExceptionNotify接口的收集和通知方法，基于队列实现。
     
   * 采集异常信息包括: 异常所在类类型、异常类型、异常详情、代码文件位置、方法名称、代码行数，异常发生时间和自定义信息等内容。
    
## 2、原理介绍:
   * 2.1、C端使用多线程生产捕捉的异常任务，以java进程为单位，任何一个java进程持有默认30000(可配置)的任务队列: 
      * 2.1.1、当配置enable为false时，表示不启用exception-monitor-starter，系统不会初始化所有相关资源；当启用但未配置server-address时，<br/>
        系统使用IExceptionNotify发送和收集通知，不启用部分线程资源。<br/>
      * 2.1.2、消费线程使用懒加载模式，当未捕获到任何异常时，不会启动消费线程资源。
      * 2.1.3、当任务队列满员时，守护线程启动熔断措施，自动停止生产者线程，从而停止异常任务生产；当任务队列有开始消费时，保护线程自动开启生<br/>
        产者任务进行生产。<br/>
      * 2.1.4、使用monitor中心模式时，C端默认与monitor端保持心跳测试，当心跳正常时才会尝试发送任务，发送失败的任务，会默认进行三次重试。<br/>
   * 2.2、通知限流，用户自定义配置通知限制规则:<br/>
      * 2.2.1、total  基于总通知量限流，同一个通知Id(如邮箱)的总量达到阈值，24h后才会接收新的通知。<br/>
      * 2.2.2、exception  基于异常类型限流，同一个通知Id(如邮箱)的同一个异常类型达到阈值，24h后才会接收新的通知。<br/>
      * 2.2.3、method  基于方法类型限流，同一个通知Id(如邮箱)的相同方法所产生异常达到阈值，24h才会接收新的通知。<br/>
      * 2.2.4、class  基于类类型限流，同一个通知Id(如邮箱)的相同类中所产生异常达到阈值，24h才会接收新的通知。<br/>
   
## 3、使用核心注解: @ExceptionMonitor
   * 注解提供继承性，建议不要使用在baseService,baseController等基础base类上，建议放在具体接口service、方法、baseMapper上。
   * 注解参数: <br/>
     showExceptionDetail    是否提供异常更详情信息(包含所有异常)，默认true。<br/>
     ignoreExceptionName    需要忽略的异常类名,支持类名称简写和全路径名称(默认忽略大小写)。<br/>
     customizedInfo         自定义定制化信息,便于定位或者特殊标出。<br/>
     noticeEmails           异常邮件提示地址，多个使用英文逗号隔开，优先级高于全局系统配置。<br/>
## 4、使用示例:
   * java:
   ````
   @GetMapping("/order/list/{id}")
   @ExceptionMonitor(ignoreExceptionName = {"classnotfoundexception","serviceException","java.lang.IllegalArgumentException"},
               customizedInfo = "这个列表查询接口",showExceptionDetail = false,noticeEmails = {"Alibaba.Jack@miniso.com,David.Jack@tencent.com"})
   public Object list(@PathVariable("id")String id){
       int i =1/0;
       return i;
   }
   ````
   
   * 配置文件application-prod.yml:
   ````
    monitor:
        #是否开启异常捕获内容
        enable: true
        #当前系统名称(用于提示区分不同系统)
        system-name: scm
        #监控中心服务地址(不配置则默认IExceptionNotify实现接口)
        server-address: http://172.18.253.4:6699
        #获取提示邮件地址，多个使用英文逗号隔开
        receive-email: wuying.ma@miniso.com,Java.ma@miniso.com
        #企业微信Id，多个使用英文逗号隔开
        receive-wechat: Jack Ma,Jdk Ma
        #最大任务数，默认30000(当monitor端网络问题无法连接时，避免任务队列内存OOM，进行限制)
        max-queue-size: 50000
        flow:
            #是否开启通知限流
            enable: true
            #限流规则
            ruleType: total
            #限流邮件数量
            num: 200
   ```` 
   