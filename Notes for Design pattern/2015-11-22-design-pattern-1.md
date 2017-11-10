---
title:设计模式之策略模式
date: 2015-11-22 08:27:32
tags: design-pattern
categories: About Java
---

软件开发过程中，实现一个功能往往有多种方法，可以根据环境或者条件选择不同的算法和方法来实现。一般的方式称为**硬编码**即在一个方法中封装了大量的方法或者算法，然后根据 if...else 或者 case 来选择不同的方法，另一种就是一个类中提供了许多方法或者算法，每一个函数都对应着一个。这两种方式都称为硬编码，设想要增加或者更改一个方法，维护起来是比较麻烦的，一旦方法多了的情况下。策略模式：定义了算法簇，分别封装起来，让他们之间可以互相替换(意味着实现的功能以及最后的目的是一样的)，此方法让算法独立于使用算法的客户。

### 角色定义

* 环境类(Context):用一个 ConcreteStrategy 对象来配置。维护一个对 Strategy 对象的引用。可定义一个接口来让 Strategy 访问它的数据。
* 抽象策略类(Strategy):定义所有支持的算法的公共接口。 Context 使用这个接口来调用某 ConcreteStrategy 定义的算法。
* 具体策略类(ConcreteStrategy):继承 Strategy 接口实现某具体算法。

### 代码讲解

策略接口：
``` java
interface IStrategy {
    public void doSomething();
}
```

具体策略实现类：
``` java
class ConcreteStrategy1 implements IStrategy {
    public void doSomething() {
        System.out.println("具体策略1");
    }
}
class ConcreteStrategy2 implements IStrategy {
    public void doSomething() {
        System.out.println("具体策略2");
    }
}
```

上下文环境类：
``` java
class Context {
    private IStrategy strategy;

    public Context(IStrategy strategy){
        this.strategy = strategy;
    }

    public void execute(){
        strategy.doSomething();
    }
}
```

客户测试类：
``` java
 public class Client {
     public static void main(String[] args){
        Context context;
        System.out.println("-----执行策略1-----");
        context = new Context(new ConcreteStrategy1());
        context.execute();

        System.out.println("-----执行策略2-----");
        context = new Context(new ConcreteStrategy2());
        context.execute();
        }
    }
```

### 策略模式优缺点

优点
* 消除了 一些 if...else... 条件语句
* 提供了可以替代继承的办法
* 实现的选择，可以提供相同行为的不同实现
* 易于增加扩展

缺点
* 策略模式容易产生很多策略类
* 客户端必须知道不同策略之间的区别，这时候就需要向客户暴露不同策略之间的区别
* Strategy和Context之间的通信开销  