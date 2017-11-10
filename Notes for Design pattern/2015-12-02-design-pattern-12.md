---
title: 设计模式之代理模式
date: 2016-05-04 10:50:44
tags: design-pattern
categories: About Java
---

代理模式简单点讲就是找个中间者去替代你做一些事情，而我们在其中会关心，究竟真正做事的人是替身还是你自己。如果是你自己，那替身的意义在哪里？博客后面详细分析了java动态代理底层的实现机制。代理模式：指为一个对象提供一个替身或者占位符以控制对这个对象的访问。简而言之，相当于中介的作用。


### 角色定义

* 抽象角色：声明真实对象和代理对象的**共同接口**，对应代理接口（Subject）；

* 真实角色：代理角色所代表的真实对象，是我们最终要**引用的对象**，对应委托类（RealSubject）；

* 代理角色：代理对象角色内部含有对真实对象的引用，从而可以操作真实对象，同时代理对象提供与真实对象相同的接口以便在任何时刻都能代替真实对象。同时，代理对象可以在执行真实对象操作时，附加其他的操作，相当于对真实对象进行封装，对应代理类（ProxySubject）

### 静态代理

静态代理很简单，引用一个网上的实例，参考链接见文章末尾.

``` java
// 代理接口
interface Subject{
    void doSomething();
}
```

真正的工作者:
``` java
class RealSubject implements Subject{
    @Override
    public void doSomething() {
        System.out.println("我是真正的工作者，我干了一些活");
    }
}
```

代理的工作者:
``` java
class ProxySubject implements Subject{
    // 代理类持有委托类的引用
    private Subject delegate;

    public ProxySubject(Subject delegate) {
        this.delegate = delegate;
    }

    @Override
    public void doSomething() {
        // 将请求转发给委托类进行处理
         System.out.println("我是代理类，我没怎么干活，我把活转给了别人");
         delegate.doSomething();
    }
}

代理类工厂
``` java
// 客户类调用此工厂方法获得代理对象。
// 对客户类来说，其并不知道返回的是代理类对象还是委托类对象。
class SubjectStaticFactory {

    public static Subject getInstance(){
        return new ProxySubject(new RealSubject());
    }
}
```

客户以及测试类
``` java
public class StaticProxyTest {

    public static void main(String[] args) {
        Subject proxy = SubjectStaticFactory.getInstance();
        // 代理去调用自己的 doSomething() 方法
        proxy.doSomething();
    }
}
```

静态代理不好的地方就是：委托类必须作为代理类的一个真实属性而存在。另外如果不知道事先真正的代理类是谁，怎么去委托呢？而**动态代理**就能够在运行时判断就代理委托给谁。

### 动态代理

创建动态调用处理器

``` java
public class SubjectInvocationHandler implements InvocationHandler {  
 // 代理类持有一个委托类的对象引用  
 private Object delegate;  
   
 public SubjectInvocationHandler(Object delegate) {  
  this.delegate = delegate;  
 }  
   
 @Override  
 public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {  
  long stime = System.currentTimeMillis();   
  // 利用反射机制将请求分派给委托类处理。Method的invoke返回Object对象作为方法执行结果。  
  // 因为示例程序没有返回值，所以这里忽略了返回值处理  
  method.invoke(delegate, args);  
  long ftime = System.currentTimeMillis();   
  System.out.println("执行任务耗时"+(ftime - stime)+"毫秒");  
    
  return null;  
 }  
} 
```

生成代理对象的工厂:
``` java
public class DynProxyFactory {  
 // 客户类调用此工厂方法获得代理对象。  
 // 对客户类来说，其并不知道返回的是代理类对象还是委托类对象。  
 public static Subject getInstance(){   
  Subject delegate = new RealSubject();  
  InvocationHandler handler = new SubjectInvocationHandler(delegate);  
  Subject proxy = null;  
  proxy = (Subject)Proxy.newProxyInstance(  
    delegate.getClass().getClassLoader(),   
    delegate.getClass().getInterfaces(),   
    handler);  
  return proxy;  
 }  
}
```

客户测试类：
``` java
public class Client {  
 public static void main(String[] args) {  
  // 利用工厂生成实例
  Subject proxy = DynProxyFactory.getInstance();  
  proxy.dealTask("DBQueryTask");  
 }   
}
```


**注意1:**Proxy类的静态方法newProxyInstance()非常重要，它简化了许多过程，从而直接去创建动态代理类，其本质也还是反射.

**注意2:**再看一下invoke()方法,handler 是关键，联系起来Proxy了，一旦Proxy内的方法被调用，就会通过handler触发其内部的 invoke() 方法。


```


