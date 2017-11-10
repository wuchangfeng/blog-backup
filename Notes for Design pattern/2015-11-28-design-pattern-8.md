---
title: 设计模式之适配器模式
date: 2016-04-22 19:18:44
tags: design-pattern
categories: About Java
---


适配器模式是将一个类的接口，转化成客户所期望的另一个接口。适配器让原本不兼容的类，可以合作无间。

## 实例引入

鸭子接口：
``` java
interface Duck{
    // 会叫
    void quack();
    // 会飞
    void fly();
}
```

绿头鸭实现类：
``` java
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
    // 吞并
    void gobble();
    // 会飞
    void fly();
}
```

火鸡实现类：
``` java
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

现在场景是缺鸭子对象，想用火鸡来冒充，因此有了适配器实现类：
``` java
class TurkeyAdapter implements Duck{
    Turkey turkey;
    // 取得要适配的对象的引用
    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey;
    }
    // 实现接口中的方法
    @Override
    public void quack() {
        turkey.gobble();
    }
    // 为了让火鸡模仿的像一只鸭子，所以要连续跳五次
    @Override
    public void fly() {
        for (int i = 0; i < 5; i++) {
            turkey.fly();
       	}
    }
}
```

鸭子测试类：
``` java
public class DuckTestDriver {
    public static void main(String[] args) {
        MallardDuck duck = new MallardDuck();
        WildTurkey turkey = new WildTurkey();
        // 将火鸡包装进一个火鸡适配器中，使其像一只鸭子
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

角色定义：

* 客户：其根据目标接口实现的，这里就是鸭子
* 适配器：适配器也实现了目标接口，这里就是适配器类实现了 Duck 接口，并持有被适配者的实例(即 Turkey 实例)
* 被适配者：被适配者同样也实现了被适配者接口，这里火鸡就是被适配者接口，它要变化成鸭子。

客户使用适配器的过程如下：

* 客户通过目标接口调用适配器的方法对适配器发出请求
* 适配器使用被适配者接口把请求转化成被适配者的一个或者多个调用接口
* 客户接收到调用的结果，但并未察觉这一切是适配器在起转化作用


## Java中的实例

* 旧世界的枚举器：早期集合类型(Vector,Hashtable)都实现了 Enumeration 接口，其内的方法可以一步一步的列出结合中所有的元素，但是枚举相当于**只读**。
* 新世界的枚举器：后来采用了 iterator 接口，与枚举接口不同的是，它还额外提供了删除元素的能力。

而由此我们就像将枚举适配到迭代器，我们可以看看实例代码：

适配器类：
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

其实现了 Iterator 接口即目标接口，而目标接口定义如下：
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

仔细看看上面的程序，面向接口编程的思想真是淋漓尽致。


