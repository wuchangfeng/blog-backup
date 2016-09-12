---
title: 设计模式入门
date: 2016-04-01 08:27:32
tags: design-pattern
categories: About Java
---

Head first 设计模式-设计模式入门，面向接口编程入门，**策略模式**入门

* 封装的概念进一步引申

* 针对接口(超类型)编程，不要针对实现编程，实现解耦

* 用组合而不用继承的好处

<!-- more -->


### 一 . 设计原则

*  要把项目中经常变化的部分，给单独抽离出来，封装起来，让这一部分经常变化的不会影响到其他的。

*  针对接口编程，而不是针对实现编程。

*  多用组合,少用继承


### 二 . 实例代码

这个我们经常看到，子类对象交给父类的引用，去执行父类所拥有的方法，这样子类就不用去管，自己所需要的方法是怎么实现的了，只需要知道： 啊，父类有这个方法，我只管用就行了，昂。

看到这里我就想问，出了上面的好处，还有别的吗？

我个人的想法是，这样做就只需要在运行时，才需要指定具体的实现对象，这样就避免了还在编译的时候，就绑定了，这样对内存效率什么的不好吧？

另外看一个针对实现编程的：

``` java
	Dog d = new Dog();
	d.wang();

	Cat c = new Dog();
	c.miao();

```

这样做不挺好的吗？但是加入有100种动物，写100种叫法？所以这就是针对实现编程的一个弊端之一。之二就涉及到解耦了，100 类狗都调用了 wang(),但是 我想改成wangwang(),那我还要在 100 个调用这个wang() 一个一个去该？WTF！

针对接口编程就不一样了：
    
``` java
	Animal a = new Dog();
	a.jiao();

```

只要把具体的对象指定给父类的引用，子类就他妈不用管了，我就把我自己传进去了，怎么叫，由爸爸来决定。呵呵


### 三 . 关于组合的实例

首先要了解什么是组合？ Java Se 中有啊，为什么我感觉就是引用呢？

首先组合肯定要比较继承啊，它们都是实现类的复用的重要的方式，但是继承不好的地方就是会破坏封装，有道理昂。子类继承父类，会造成**严重耦合**，说道这里，我们就要严肃一下，什么时候用继承了：

* 子类额外增加属性，而不仅仅是属性值的改变
* 子类需要增加自己独有的行为方式

继承（is - a ），组合（has - a）

另外关于效率的问题：

继承关系是编译时确定的，即静态复用，而组合 之间的关系 为运行时确定的，在没有运行时，整体类是不会知道自己将持有特定接口下的那个实现类。在扩展方面组合比集成更具有广泛性。


牛逼的代码来了，来自设计模式第一章，代码很简单，但是要理清之间逻辑关系，以及为什么这么做，这么做有什么好处，还是要费点功夫滴。

``` java
// 行为接口
interface FlyBehavior{

    	public void fly();
	}
	// 行为类
	class FlyWithWings implements FlyBehavior{

    @Override
    public void fly() {

        System.out.println("i am flying");
    	}
	}
    // 行为类
	class FlyNoWay implements FlyBehavior{

    	@Override
    	public void fly() {
    	    System.out.println("i can not fly");
    	}
	}

	class FlyRocketPowered implements FlyBehavior{
    @Override
    public void fly() {
        System.out.println("I am flying with rocket");
    }
	}
	// 行为接口
	interface QuackBehavior{
    	public void quack();
	}
	// 行为实现类
	class Quack implements QuackBehavior{
    @Override
    public void quack() {
        System.out.println("Quack");
    }
	}
	// 行为实现类
	class MuteQuack implements QuackBehavior{
    @Override
    public void quack() {
        System.out.println("slience");
    }
	}
	// 行为实现类
	class Squeak implements QuackBehavior{
    @Override
    public void quack() {
        System.out.println("Squeak");
    }
	}
	// 基类
	abstract class Duck {
    FlyBehavior flyBehavior;
    QuackBehavior quackBehavior;

    public Duck(){}

    public abstract void display();

    public void performFly(){
		// 委托给实现类
        flyBehavior.fly();
    }
    public void performQuack(){
        quackBehavior.quack();
    }
    // 给鸭子添加行为
    public void setFlyBehavior(FlyBehavior fb){
        flyBehavior = fb;
    }
    public void setQuackBehavior(QuackBehavior qb){
        quackBehavior = qb;
    }
    public void swim(){
        System.out.println("we all can swim");
    }
	}
	// 子类
	class MallardDuck extends Duck{
    public MallardDuck() {
        quackBehavior = new Quack();
        flyBehavior = new FlyWithWings();
    }
    @Override
    public void display() {
        System.out.println("I am a mallardduck");
    }
	}
    // 模型鸭子
	class ModeDuck extends Duck{
    public ModeDuck() {
        quackBehavior = new Quack();
        flyBehavior = new FlyNoWay();
    }
    @Override
    public void display() {
        System.out.println("I am a modelduck");
    }
	}
    // 测试类
	public class MiniDuckSimulator {

    public static void main(String[] args) {

        System.out.println("======绿头鸭子=====");
        Duck mallard = new MallardDuck();
        mallard.performQuack();
        mallard.performFly();
        //mallard.display();
        System.out.println("======模型鸭子=====");
        Duck model = new ModeDuck();
        model.performFly();
        model.setFlyBehavior(new FlyRocketPowered());
        model.performFly();
    }
}
```

书上一句话很关键，若想在运行时，改变鸭子的行为，只需要调用 setter 方法就可以了，意味着我们不需要去改变里面的代码，即称为动态设定行为昂。

 **在上面我们不知不觉的用到了组合，因为鸭子的行为，我们不是从父类继承过来的，而是动态绑定即组合过来的**。


另外，书上说，我们用到了策略模式，WTF，有吗？什么叫策略模式？

### 四 . 策略模式

软件开发过程中，实现一个功能往往有多种方法，可以根据环境或者条件选择不同的算法和方法来实现。一般的方式称为**硬编码**即在一个方法中封装了大量的方法或者算法，然后根据 if...else 或者 case 来选择不同的方法，另一种就是一个类中提供了许多方法或者算法，每一个函数都对应着一个。这两种方式都称为**硬编码**，设想要增加或者更改一个方法，维护起来是比较麻烦的，一旦方法多了的情况下。

**策略模式：**定义了算法簇，分别封装起来，让他们之间可以**互相替换**(意味着实现的功能以及最后的目的是一样的)，此方法让算法独立于使用算法的客户。

策略模式组成分为以下三类：

1. 环境类(Context):用一个 ConcreteStrategy 对象来配置。维护一个对 Strategy 对象的引用。可定义一个接口来让 Strategy 访问它的数据。

2. 抽象策略类(Strategy):定义所有支持的算法的公共接口。 Context 使用这个接口来调用某 ConcreteStrategy 定义的算法。

3. 具体策略类(ConcreteStrategy):继承 Strategy 接口实现某具体算法。


### 五 . 总结

优点

* 消除了 一些 if...else... 条件语句

* 提供了可以替代继承的办法

* 实现的选择，可以提供相同行为的不同实现

缺点

* 策略模式容易产生很多策略类

* 客户端必须知道不同策略之间的区别，这时候就需要向客户暴露不同策略之间的区别

* Strategy和Context之间的通信开销  