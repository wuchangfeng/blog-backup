---
title: 命令模式
date: 2016-04-20 20:16:01
tags: design-pattern
categories: About Java
---
Head first 设计模式-命令模式

<!-- more -->

## 定义


命令（Command）模式：又称Action模式或者Transaction模式。它属于对象的行为模式。命令模式把一个请求或者操作封装到一个对象中。命令模式允许系统使用不同的请求把客户端参数化，对请求排队或者记录请求日志，可以提供命令的撤销和撤销功能。

命令模式是对命令的封装，它把发出命令的责任和执行命令的责任分开，委派给不同的对象。每一个命令都是一个操作，请求的一方发出请求要求执行一个操作，接受的一方收到请求并执行操作。命令模式允许请求的一方和接受的一方独立开来，使得请求的一方不必知道接收请求的一方接口，更不必知道请求是怎么被接收，以及操作是否被执行，何时被执行，以及是怎么被执行的。命令允许请求方和接收方各自都能独立演化，从而具有以下优点：

* 命令模式使新的命令在不改变现有结构代码的情况下很容易被加入到系统里
* 允许接收请求的一方决定是否否决请求
* 能较容易地设计一个命令队列
* 可以容易地实现对请求的Undo和Redo操作
* 在需要的情况下以较容易地将命令记入日志


## 结构

* 抽象命令(Command)角色：声明执行操作的接口,**一般是一个接口或者抽象类**,把请求或者操作封装在其中
* 具体命令(ConcreteCommand)角色：将一个接受者对象绑定到一个动作上,调用接受者相应的操作，以实现 Execute 方法,就是命令的实现类
* 客户端(Client)角色：创建一个命令对象并设定它的接受者
* 请求者(Invoker)角色：负责调用命令对象的执行请求,直白点就是**命令的发出者**
* 接受者(Receiver)角色：负责具体实施和执行一个请求相关的操作。任何一个类都可以作为一个接受者

## 模板实例

Command:可以有执行和撤销的方法

``` java
	interface Command{
	  void execute();
	}
```

Command的实现类：MyCommand，这里命令的实现者也可以有多个

``` java
	public class MyCommand implements Command {  
    private Receiver receiver;  
  
    public MyCommand(Receiver receiver) {  
        this.receiver = receiver;  
    }  
  
    @Override  
    public void execute() {  
        receiver.action();  
    	}  
	}
```

Receiver(命令的接收者,执行者):从这里看，命令的执行者有相当大的自由空间，想干嘛干嘛

``` java
	public class Receiver {  

    public void action() {  
        System.out.println("I have received the command!");  
    	}  
	}
```
 Invoker(命令的请求者):当然，这里的命令请求者，也可以有多个。另外对于下面这段代码，其为命令的调用者，可以明显感觉到调用者根本没有跟命令的执行者有丝毫的交互，根本不用管命令的执行者怎么去执行命令的。

``` java
	public class Invoker {  
    private Command command;  
  
    public Invoker(Command command) {  
        this.command = command;  
   	    }  

    public void action() {  
        command.execute();  
	    }  
	} 
```

TestCommand测试类：

``` java
	public class TestCommand {  
  
    public static void main(String[] args) {  
        Receiver receiver = new Receiver();  
        Command command = new MyCommand(receiver);  
        Invoker invoker = new Invoker(command);  
        invoker.action();  
    	}  
	}
```

上面看来：命令模式就是进行**命令的封装**，将命令的**发出者**和命令的**执行者**分隔开，委派给不同的对象，这样实现解耦。这样即使需求发生变化，也只需修改部分模块的代码，比如命令发生变化，只需修改其**命令的实现类**，命令接受者发生变化只需修改Receiver，命令的发出者发生变化就只需修改Invoker。


## 场景实例

[实例参考链接](http://blog.csdn.net/lhy_ycu/article/details/39804057),非常形象生动。

示例：以去饭店吃饭为例子

1.和小二说，来个宫保鸡丁 --> 顾客发出口令 
  
2.小二来了一句：宫保鸡丁一份。 这时命令被传递到了厨师。--> 口令传递到了厨师 
   
3.然后厨师就开始做宫保鸡丁去了。 --> 厨师根据口令去执行 
  
从这3步可以看到，宫保鸡丁并不是我想吃就我来做，而是传达给别人去做。 我要的是一个结果——宫保鸡丁这道菜做好了，而我无需去关系这道菜是怎么去做的。 

抽象命令角色(Command)

``` java
	interface Command {  
      //口令执行 
      public void execute();  
      //口令撤销  
      public void undo();  
   }
```

口令 -- 经小二传递，具体命令(ConcreteCommand)角色

``` java
	class OrderCommand implements Command {  
      private CookReceiver cook;  
  
      public OrderCommand(CookReceiver cook) {  
          this.cook = cook;  
      }  
  
      @Override  
      public void execute() {  
          cook.cooking();  
      }  
  
      @Override  
      public void undo() {  
          cook.unCooking();  
      }  
   }
```

厨师--真正的口令执行者，它就是命令的接受者(Receiver)角色
 
``` java
	class CookReceiver {  

    public void cooking() {  
        System.out.println("开始炒宫保鸡丁了...");  
    }  
  
    public void unCooking() {  
        System.out.println("不要炒宫保鸡丁了...");  
     }  
	} 
```

顾客--真正的口令发出者，即模板中的 Invoker，同样在这里我们并没有去直接调用厨师的 cook() 方法

``` java
	class Customer {  
    private Command command;  
  
    public Customer(Command command) {  
        this.command = command;  
    }  
  
    //将命令的发出与执行分开  
    public void order() {  
        command.execute();  
    }  
  
    public void unOrder() {  
        command.undo();  
    }  
	} 
```

客户端测试类

``` java
	public class Test { 

    	public static void main(String[] args) {  
        //等待口令的执行者 --炒菜总得有个厨师吧.   
        CookReceiver receiver = new CookReceiver();  
        //等待将口令传达给厨师 --因为顾客要什么菜还不知道，但口令始终要传达到厨师耳朵里这是肯定的。 
        Command cmd = new OrderCommand(receiver);  
        Customer customer = new Customer(cmd);  
        //执行口令   
        customer.order();  
        //撤销口令   
        customer.unOrder();  
   	 }  
	} 
```


## 总结 1

* 命令模式中，**请求者不直接和接受者进行交互**，即请求者不包含接受者的应用，这样消除了彼此之间的耦合。即有命令这个中间件。

* 命令者模式满足了软件的“开-闭原则”。如果增加新的具体命令和该命令的接受者，不必修改调用者的代码，调用者就可以直接使用新的命令对象。反之如果增加新的调用者，不必修改现有的具体命令和接受者。新增加的调用者就可以使用已有的具体命令。

*　由于请求者的请求被封装到了具体命令中，那么就可以将具体命令保存到持久化媒介中，在需要的时候重新执行这个具体命令。因此使用命令者模式可以记录日志

* 使用命令者模式可以对请求者的请求进行排队，每个请求者各自对应一个具体命令，因此可以按一定的顺序执行这些命令。

以上总结来自 [java设计模式之命令模式](http://blog.csdn.net/u010142437/article/details/12362173)



* 
