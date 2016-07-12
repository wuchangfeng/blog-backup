---
title: 适配器模式和外观模式
date: 2016-04-22 19:18:44
tags: design-pattern
categories: About Java
---
Head first 设计模式-适配器模式&外观模式

<!-- more -->

## 定义

**适配器模式**：将一个类的接口，转化成客户所期望的另一个接口。适配器让原本不兼容的类，可以合作无间。

## 引入

鸭子接口以及其实现类

``` java
	interface Duck{

    void quack();
    void fly();
	}
	//绿头鸭
	class MallardDuck implements Duck{

    @Override
    public void quack() {
        System.out.println("Quack");
    }

    @Override
    public void fly() {
        System.out.println("Flying");
     }
	}
```

火鸡接口以及其实现类

``` java
	interface Turkey{

    void gobble();
    void fly();
	}
	//火鸡
	class WildTurkey implements Turkey{

    @Override
    public void gobble() {
        System.out.println("gobble,gobble");
    }

    @Override
    public void fly() {
        System.out.println("i am flying short distance");
     }
	}
```

适配器实现类

``` java
	//现在场景是缺鸭子对象，想用火鸡来冒充
	class TurkeyAdapter implements Duck{
    Turkey turkey;

    //取得要适配的对象的引用
    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey;
    }
    //实现接口中的方法
    @Override
    public void quack() {
        turkey.gobble();
    }
    //为了让火鸡模仿的像一只鸭子，所以要连续跳五次
    @Override
    public void fly() {
        for (int i = 0; i < 5; i++) {
            turkey.fly();
       		 }
    	}
	}
```

测试类

``` java
	public class DuckTestDriver {

    public static void main(String[] args) {

        MallardDuck duck = new MallardDuck();
        WildTurkey turkey = new WildTurkey();
        //将火鸡包装进一个火鸡适配器中，使其像一只鸭子
        Duck turkeyAdapter = new TurkeyAdapter(turkey);

        System.out.println("The turkey says:");
        turkey.gobble();
        turkey.fly();

        System.out.println("\n The Duck says:");
        testDuck(duck);

        System.out.println("\n The TurkeyAdapter says:");
        testDuck(turkeyAdapter);
    	}

     static void testDuck(Duck duck){
        duck.quack();
        duck.fly();
     }
	}
```

* **客户**：其根据目标接口实现的，这里就是鸭子
* **适配器**：适配器也实现了目标接口，这里就是适配器类实现了 Duck 接口，并持有被适配者的实例(即 Turkey 实例)
* **被适配者**：被适配者同样也实现了被适配者接口，这里火鸡就是被适配者接口，它要变化成鸭子。

客户使用适配器的过程如下：

* 客户通过目标接口调用适配器的方法对适配器发出请求
* 适配器使用被适配者接口把请求转化成被适配者的一个或者多个调用接口
* 客户接收到调用的结果，但并未察觉这一切是适配器在起转化作用


## 实例(java)

* **旧世界的枚举器**：早期集合类型(Vector,Hashtable)都实现了 Enumeration 接口，其内的方法可以一步一步的列出结合中所有的元素，但是枚举相当于**只读**。
* **新世界的枚举器**：后来采用了 iterator 接口，与枚举接口不同的是，它还额外提供了删除元素的能力。

而由此我们就像将枚举适配到迭代器，我们可以看看实例代码：


适配器类

``` java
	class EnumerationIterator implements Iterator{
    Enumeration enumeration;

    public EnumerationIterator(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        System.out.println("++++++");
        return enumeration.hasMoreElements();
    }

    @Override
    public Object next() {
        System.out.println("-----");
        return enumeration.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
	}
```

其实现了 Iterator 接口 即目标接口，而目标接口定义如下：

``` java
	public interface Iterator<E> {
		boolean hasNext();
		E next();
		default void remove() {
        throw new UnsupportedOperationException("remove");
    	}
		//...
	}
```

而 Enumeration 为被适配接口，其定义如下：

``` java
	public interface Enumeration<E> {
		boolean hasMoreElements();
		E nextElement();
	}
```

想 Vector 和 Hashtable 都实现了 Enumeration 接口，因此我们可有如下测试类：

``` java
	public class EtoITest {

    public static void main(String[] args) {

        Vector v = new Vector(Arrays.asList(args));
        v.add("a");
        v.add("b");
        v.add("c");
        Iterator iterator = new EnumerationIterator(v.elements());
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
         }
    	}
	}
```

仔细看看上面的程序，面向接口编程的思想真是淋漓尽致


## 定义

**外观模式**：为了简化接口，进而改变接口，将一个或者数个类的复杂影藏在背后，只露出一个干净美好的外观

## 实例场景

构造舒适的家庭影院，我们需要进行以下这些麻烦操作：

1. 打开爆米花机
2. 开始爆米花
3. 将灯光调暗
4. 放下屏幕
5. 打开投影机
6. ....
7. ....
8. ....
9. ....
10. ....
11. ....
12. ....
13. 开始播放 DVD

可以看见，为了看一部电影，要进行这么多操作，很麻烦。不仅如此，关闭的时候也是这样。而**外观模式**提供了一个更合理的外观类，可以将一个复杂的子系统变的很容易使用。并且外观只是提供你更直接的操作，并未将原来的子系统阻隔(封装)起来，如果需要使用底层的，也还是可以的。**另外**外观不只是简化了接口，同时也将客户从组建的子系统中解耦。

代码如下，可以仔细体会一下：

``` java
	public class HomeTheaterFacade{
		Amplifier amp;
		Tuner tuner;
		DvdPlayer dvd;
		CdPlayer cd;
		Projecter pro;
		TheaterLights tl;
		Screen sc;
		PopcornPopper pp;
		
		Public HomeTheaterFacade(
		Amplifier amp,
		Tuner tuner,
		DvdPlayer dvd,
		CdPlayer cd,
		Projecter pro,
		TheaterLights tl,
		Screen sc,
		PopcornPopper pp;
		) {
			this.amp = amp;
			...
			this.sc = sc;
			this.pp = pp;
		}
		
		public void WatchMovie(String movie){
			
			sout("start");
			amp.start();
			...
			dvd.play(movie);
		}

		public void EndMovie(){
			
			sout("start");
			amp.off();
			...
			dvd.off();
		}
	 }

	public class HomeTheaterTestDriver{
		psvm(String args[]){
			//省略实例化的组件

            //根据子系统的所有组件来实例化外观
			HomeTheaterFacade ht = new HomeTheaterFacade(amp....,pp);
			//使用简化的接口
			ht.watchMovie("Red Dog");
			ht.ednMovie();
		}
	}
```


## javaAPI中涉及

JDBC的设计就是典型的外观模式，封装了数据库的连接过程和对数据的操作，隐藏了具体细节。要是没有JDBC，我们就要针对不同的数据库（DB2，ORACLE，SQL Server）去操作

由此也引入了**设计原则**：最少知识原则，只和密友谈话。个人理解，简单点说：不要一个方法调用一个方法，即太多类耦合在一起，免得修改其中的一部分影响到另外许多类，即类与类之间有太多依赖

另外，我们可以想到在 java 中 "System.out.println()"违反了**最少原则**

## 总结

外观和最少知识原则，对于 Client 来说，它只有一个"朋友"也就是 HomeTheaterFacade ，它帮助
Client 管理 全部子系统组件，并且我们可以在不影响客户的情况下升级这些组件

这一章经常说道**装饰者**，**适配器**，**外观模式**，下面比较一下其中的用法区别：

* 适配器将一个对象包装起来以改变其接口
* 装饰者将一个对象包装起来以增加新的行为和责任
* 外观则将一群对象包装起来简化其接口