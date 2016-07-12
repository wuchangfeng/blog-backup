---
title: 代理模式
date: 2016-05-04 10:50:44
tags: design-pattern
categories: About Java
---

Head first 设计模式-代理模式

代理模式简单点讲就是找个中间者去替代你做一些事情，而我们在其中会关心，究竟真正做事的人是替身还是你自己？如果是你自己，那替身的意义在哪里？

博客后面详细分析了 java 动态代理底层的实现机制。


<!-- more -->

### 一 . 定义及类图

代理模式：指为一个对象提供一个替身或者占位符以控制对这个对象的访问。简而言之，相当于中介的作用。

代理模式类图如下：

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E4%BB%A3%E7%90%86%E6%A8%A1%E5%BC%8F.jpg)

代理涉及的角色有以下：

抽象角色：声明真实对象和代理对象的**共同接口**，对应代理接口（Subject）；

真实角色：代理角色所代表的真实对象，是我们最终要**引用的对象**，对应委托类（RealSubject）；

代理角色：代理对象角色内部含有对真实对象的引用，从而可以操作真实对象，同时代理对象提供与真实对象相同的接口以便在任何时刻都能代替真实对象。同时，代理对象可以在执行真实对象操作时，附加其他的操作，相当于对真实对象进行封装，对应代理类（ProxySubject）

### 二 . 静态代理(1)

静态代理很简单，引用一个网上的实例，参考链接见文章末尾.

``` java
// 代理接口
interface Subject{

    void doSomething();
}

// 真正的工作者
class RealSubject implements Subject{

    @Override
    public void doSomething() {
        System.out.println("我是真正的工作者，我干了一些活");
    }
}

// 代理的工作者
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

// 代理类工厂
// 客户类调用此工厂方法获得代理对象。
// 对客户类来说，其并不知道返回的是代理类对象还是委托类对象。
class SubjectStaticFactory {

    public static Subject getInstance(){
        return new ProxySubject(new RealSubject());
    }
}

// 客户以及测试类
public class StaticProxyTest {

    public static void main(String[] args) {
        Subject proxy = SubjectStaticFactory.getInstance();
        // 代理去调用自己的 doSomething() 方法
        proxy.doSomething();
    }
}
```

静态代理不好的地方就是：委托类必须作为代理类的一个真实属性而存在。另外如果不知道事先真正的代理类是谁，怎么去委托呢？而**动态代理**就能够在运行时判断就代理委托给谁。

### 三 . 动态代理(2)

创建动态调用处理器

``` java
/** 
 * 动态代理类对应的调用处理程序类 
 */  
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

生成代理对象的工厂

``` java
/** 
 * 生成动态代理对象的工厂. 
 */  
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

客户测试类

``` java
public class Client {  
  
 public static void main(String[] args) {  
  // 利用工厂生成实例
  Subject proxy = DynProxyFactory.getInstance();  
  proxy.dealTask("DBQueryTask");  
 }   
}
```


**注意1:**Proxy类的静态方法 newProxyInstance() 非常重要，它简化了许多过程，从而直接去创建动态代理类，其本质也还是反射.

**注意2:**再看一下 invoke() 方法,handler 是关键，联系起来 Proxy 了，一旦 Proxy 内的方法被调用，就会通过 handler 触发其内部的 invoke() 方法。


### 四 . 动态代理(HF)

java.lang.reflect 包中有自己的代理支持，利用此包可以在运行时动态的创建一个代理类，实现一个或者多个接口，并将方法调用转发到你指定的类。实际的代理类实在运行时创建的，故而称为动态代理。动态代理类的源码时程序运行期间根据反射等机制动态的生成。

#### 动态代理类图
[图片链接](http://www.cnblogs.com/mengdd/archive/2013/05/07/3065619.html)


![](http://7xrl8j.com1.z0.glb.clouddn.com/%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E7%B1%BB%E5%9B%BE.jpg)

#### 实例场景

对象村要实现一个约会服务，在服务中有一个功能就是给一个人"Hot"和"Not"的评价，自然用户个人不能给自己来设置"Hot"和"Not"，但是可以设置自己的一些信息。而其他用户就能给你打分，但是不能修改你的属性信息。这个场景是一个可以使用保护代理的绝佳例子，一种根据访问权限决定对象是否能访问对象的代理。

#### 设计步骤

* 创建两个 InvocationHandler

* 写代码创建动态代理

* 利用适当的代理包装任何 PersonBean 对象

### 五 . 与动态代理相关的 API

Proxy 的核心方法

下面的 newProxyInstance() 就是底下实例的核心方法

``` java
// 获取指定代理对象所关联的调用处理器
public static InvocationHandler getInvocationHandler(Object proxy)

// 用于为指定类装载器、一组接口及调用处理器生成动态代理类实例   
public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
// 判断是否是动态代理类
public static boolean isProxyClass(Class<?> cl)

// 用于获取关联于指定类装载器和一组接口的动态代理类的类对象
public static Class<?> getProxyClass(ClassLoader loader,
                                         Class<?>... interfaces)

// 核心方法
public Object invoke(Object proxy, Method method, Object[] args)

```

InvocationHandler 核心方法

invoke() 负责处理动态代理类上的所有方法调用

``` java
// 该方法负责集中处理动态代理类上的所有方法调用。第一个参数既是代理类实例，第二个参数是被调用的方法对象  
// 第三个方法是调用参数。调用处理器根据这三个参数进行预处理或分派到委托类实例上反射执行
public Object invoke(Object proxy, Method method, Object[] args)
```

### 六 . 实例代码

javaBean 接口

``` java
public interface PersonBean {
 
	String getName();
	String getGender();
	String getInterests();
	int getHotOrNotRating();
 
    Public void setName(String name);
    Public void setGender(String gender);
    Public void setInterests(String interests);
    Public void setHotOrNotRating(int rating); 
}
```

personBean 的实现就省略了，很简单。

创建 Invocationhandler ，其中的 OWerInvocationhandler 

``` java
public class OwnerInvocationHandler implements InvocationHandler { 
	PersonBean person;
 	// 持有person的引用
	public OwnerInvocationHandler(PersonBean person) {
		this.person = person;
	}
 	// 每次 proxy 的方法被调用，就会导致 proxy 调用此方法
	public Object invoke(Object proxy, Method method, Object[] args) 
			throws IllegalAccessException {
  
		try {
			// get 则可调用 person 内的方法
			if (method.getName().startsWith("get")) {
				// 通过 invoke 去调用 person 内的 get 方法
				return method.invoke(person, args);
			// 自己不能给自己的分数
   			} else if (method.getName().equals("setHotOrNotRating")) {
				throw new IllegalAccessException();
			// set 可以随便用，因为我们是拥有者
			} else if (method.getName().startsWith("set")) {
				return method.invoke(person, args);
			} 
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } 
		return null;
	}
}
```

创建 Proxy 类并且实例化

``` java
/**
 * @param person
 * @return 代理和主题有同样的接口所以也返回 PersonBean
 */
PersonBean getOwnerProxy(PersonBean person) {
 		// 利用 proxy 类的静态 newProxyInstance 创建代理
        return (PersonBean) Proxy.newProxyInstance( 
            	person.getClass().getClassLoader(),
            	person.getClass().getInterfaces(),
				// 能够访问真是主题的原因
                new OwnerInvocationHandler(person));
	}
```

测试服务

``` java
public class MatchMakingTestDrive {
	Hashtable datingDB = new Hashtable();
 	
	public static void main(String[] args) {
		MatchMakingTestDrive test = new MatchMakingTestDrive();
		test.drive();
	}
 
	public MatchMakingTestDrive() {
		initializeDatabase();
	}

	public void drive() {
		// 从数据库中抽取一个人出来
		PersonBean joe = getPersonFromDatabase("Joe Javabean");
		// 创建拥有者代理
		PersonBean ownerProxy = getOwnerProxy(joe);
		System.out.println("Name is " + ownerProxy.getName());
		ownerProxy.setInterests("bowling, Go");
		System.out.println("Interests set from owner proxy");
		try {
			ownerProxy.setHotOrNotRating(10);
		} catch (Exception e) {
			System.out.println("Can't set rating from owner proxy");
		}
		System.out.println("Rating is " + ownerProxy.getHotOrNotRating());
		// 创建一个非拥有者代理
		PersonBean nonOwnerProxy = getNonOwnerProxy(joe);
		System.out.println("Name is " + nonOwnerProxy.getName());
		try {
			nonOwnerProxy.setInterests("bowling, Go");
		} catch (Exception e) {
			System.out.println("Can't set interests from non owner proxy");
		}
		nonOwnerProxy.setHotOrNotRating(3);
		System.out.println("Rating set from non owner proxy");
		System.out.println("Rating is " + nonOwnerProxy.getHotOrNotRating());
	}

	/**
	 * @param person
	 * @return 代理和主题有同样的接口所以也返回 PersonBean
     */
	PersonBean getOwnerProxy(PersonBean person) {
 		// 利用 proxy 类的静态 newProxyInstance 方法创建代理
        return (PersonBean) Proxy.newProxyInstance( 
            	person.getClass().getClassLoader(),
            	person.getClass().getInterfaces(),
				// 能够访问真是主题的原因
                new OwnerInvocationHandler(person));
	}

	PersonBean getNonOwnerProxy(PersonBean person) {
		
        return (PersonBean) Proxy.newProxyInstance(
            	person.getClass().getClassLoader(),
            	person.getClass().getInterfaces(),
                new NonOwnerInvocationHandler(person));
	}

	PersonBean getPersonFromDatabase(String name) {
		return (PersonBean)datingDB.get(name);
	}

	void initializeDatabase() {
		PersonBean joe = new PersonBeanImpl();
		joe.setName("Joe Javabean");
		joe.setInterests("cars, computers, music");
		joe.setHotOrNotRating(7);
		datingDB.put(joe.getName(), joe);

		PersonBean kelly = new PersonBeanImpl();
		kelly.setName("Kelly Klosure");
		kelly.setInterests("ebay, movies, music");
		kelly.setHotOrNotRating(6);
		datingDB.put(kelly.getName(), kelly);
	}
}
```

### 七 . 概念理解

* 动态代理之所以称为动态，是因为在代码执行前还没有 proxy 类，根据需要从**传入的接口**来创建的
* InvocationHandler 根本不是 proxy ，它只是一个 proxy 的帮助类，proxy 会把调用转发给它处理
* 不管代理被调用的是何种方法，处理器被调用的一定是 invoke() 方法。示例如下：假设 proxy 的 setHotOrNot() 方法被调用

``` java	
proxy.setHotOrNotRating(9);
```

proxy 会接着调用 invocationHandler 的 invoke() 方法

``` java
invoke(Object proxy,Method method,Object[] args)
```

handler 决定要如何处置请求

``` java	
if(method.getName().invoke(person,args))
	// 调用 RealSubject 里面方法的代码
	return method.invoke(person,args);
```

这其中 person就是被调用的对象，args 就是 9，利用 method 的 get 方法我们就知道 proxy 被调用的是什么方法.method 根反射有极大的关系。


### 八 . 源码解析

newProxyInstance() 即为 Proxy 类的静态方法，其作用就是创建一个代理对象，需要以下这几个参数：

* ClassLoader loader:利用反射动态的获取代理类的类加载器
* Class<?> [] interface:利用反射获取代理类所实现的接口
* InvocationHandler h:获取实现了 InvocationHandler 的调用处理器，其作用应该是用来获取动态代理类的构造函数

``` java
 public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        // 生成代理类
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         */
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }
			// use construct generate obj
            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
			// generate obj whit cons
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }
```

 在进 getProxyClass0(loader, intfs) 的源码看看

``` java
 private static Class<?> getProxyClass0(ClassLoader loader,
                                       Class<?>... interfaces) {
     // 一个代理类最多能够实现65535个接口
    if (interfaces.length > 65535) {
        throw new IllegalArgumentException("interface limit exceeded");
    }

    // 这里Proxy做了一次缓存，如果之前生成过这个Classloader和interfaces的代理类，那么这里直接返回

  // 否则新生成类的字节码文件
   byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces);
            try {
                    // 将字节码加载到JVM
                    proxyClass = defineClass0(loader, proxyName,
                    proxyClassFile, 0, proxyClassFile.length);
              }
        retrun proxyClass;
}
```
JDK1.8 中的 getProxyClass0()

``` java
 private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
		// 根据指定的包名和接口，生成一个代理类
        return proxyClassCache.get(loader, interfaces);
}
```

源码拿出来分析确实有点难度，但是看传进去的三个参数以及返回值，就能知道它的大概工作流程了。

其中"java.lang.ClassLoader"需要注意一下：它就是类加载器负责将.class 文件(可能在磁盘上，也可能在网络上)加载到内存中，并为之生成对应的 java.lang.Class 对象，再此之后生成的类才可以被调用，而在我们生成动态代理类的时候，也需要这个类加载器对象作为参数。

### 九 . 总结与参考

[静态动态代理入门](http://blog.csdn.net/giserstone/article/details/17199755)

[细说 Java 动态代理](http://www.jianshu.com/p/0d919e54eef0#)