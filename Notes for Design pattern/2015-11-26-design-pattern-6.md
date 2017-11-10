---
title: 设计模式之单例模式
date: 2016-04-13 15:33:38
tags: design-pattern
categories: About Java
---

单例模式可以确保一个类只有一个实例，而且自行实例化并向整个系统提供这个实例。为什么需要单例模式有些对象我们只需要一个：线程池，缓存，对话框，注册表等对象，如果制造多了实例，会产生各种混淆的问题。在最后思考一个问题静态修饰符为什么与单例模式有关系？

``` java
public MyClass{
	private MyClass();
	public static MyClass getInstance(){
		return new MyClass();
	}
}
```

### 实例讲解

经典的单例模式：
``` java
public class Singleton {
	private static Singleton uniqueInstance;
	// other useful instance variables here
	private Singleton() {}
	// 构造方法一定要私有
	public static Singleton getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Singleton();
		}
		return uniqueInstance;
	}
	// other useful methods here
   }
```

注意构造器都是私有的，意味着不能随便的new出来一个单例对象。这种方法的好处就是可以延迟加载,这种模式下，不是说你调用了就给你创造出一个实例，会先进行判断。然而一切并不是这么简单，采用了上述的方法之后，程序执行过程中只会有一个实例，但是在使用多线程时候就会出现不少问题，出现**两个实例对象**，这样很容易导致一些问题，程序紊乱。

```java
public class Singleton {
	private static Singleton uniqueInstance;
	// other useful instance variables here
	private Singleton() {}

	public static synchronized Singleton getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Singleton();
		}
		return uniqueInstance;
	}
	// other useful methods here
	}
```

可以使用synchronized 这个关键字来解决问题，但是为什么书上说只是第一执行此方法时，才需要同步？原因就是每次线程都不知道 uniqueInstance 是否为空，都要进去才知道，这样就导致每次都需要去同步。
而由此带来的问题，后面再同步就是累赘了(实际上只需要第一次创建实例同步就可以了)，导致性能下降。

### 多线程情况下的单利

用"双重加锁检查"，在getInstance() 中减少使用同步。

``` java
public class Singleton {
	private volatile static Singleton uniqueInstance;
 
	private Singleton() {}
 
	public static Singleton getInstance() {
		if (uniqueInstance == null) {
			synchronized (Singleton.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Singleton();
				}
			}
		}
		return uniqueInstance;
		}
	}
```

其道理就是：首先检查实例是否创建，如果没有，才进行同步，这样一来只有在第一次才会执行同步，我们想要的就是这样啊。synchronized (Singleton.class) 这一句就是 Singleton.class 就是同步监视器，先锁定该同步监视器。涉及到 volatile 关键字的学习了。


### 静态内部类法

那么，有没有一种延时加载，并且能保证线程安全的简单写法呢？我们可以把Singleton实例放到一个静态内部类中，这样就避免了静态实例在Singleton**类加载的时候就创建对象**，并且由于静态内部类只会被加载一次，所以这种写法也是线程安全的：

```java
public class Singleton {
    private static class Holder {
        private static Singleton singleton = new Singleton();
    }

    private Singleton(){}

    public static Singleton getSingleton(){
        return Holder.singleton;
    	}
	}
```

全局变量与单间模式之间的比较记住这个模式的目的是什么？确保类只有一个实例并提供全局访问，全部变量可以提供全局访问，但是不能保证只有一个实例。另外单利模式的类，**是不能被继承的**，因为其构造器是私有的，我们不能破坏一些约定俗成的股则。

## 单例模式的优缺点

上面提到的所有实现方式都有两个共同的缺点：

* 都需要额外的工作(Serializable、transient、readResolve())来实现序列化，否则每次反序列化一个序列化的对象实例时都会创建一个新的实例。

* 可能会有人使用反射强行调用我们的私有构造器（如果要避免这种情况，可以修改构造器，让它在创建第二个实例的时候抛异常）。