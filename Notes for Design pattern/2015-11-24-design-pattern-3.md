---
title: 设计模式之装饰者模式
date: 2015-11-24 19:19:02
tags: design-pattern
categories: About Java
---

谈到装饰者模式就要与继承扯上一点关系，想想继承的不好。由此我们可以带来一种对象组合的技巧，在运行时动态的装饰类，一旦你熟悉了装饰的技巧，就能够在不修改任何**底层代码**的情况下，给你的(或别人)对象赋予新的职责。先来看看装饰者概念理解：**运行时扩展**的威力比**编译时扩展**的威力大，编译时扩展就是利用继承。我们可以利用装饰的技巧在不修改任何底层代码的情况下，来给对象赋予新的功能。另外该设计模式的初衷：一般是用继承来扩充父类的功能，但是子类如果种类很多，那么就会有很多类出来，增加了系统的复杂性，同时,使用继承实现功能拓展,我们必须可预见这些拓展功能,这些功能是编译时就确定了,**是静态的**。 静态就是编译期间预先加载到内存，如果你一个大程序，所有都用静态，你想，当你一运行该程序，你看看内存，占用率极高，那么性能就会降低。

### 角色定义

* 抽象组件类
* 组件具体实现类，也就是被装饰的对象
* 组件装饰类，内部持有一个组件对象的引用。该类为抽象的，是为了方便不同风格的装饰类
* 具体装饰类

### 实例引入

定义被装饰者，即需要扩充功能的对象或者类：
``` java
// 定义被装饰者
interface Human{
    public void wearClothes();
    public void walkToWhere();
}
```

定义用来修饰被装饰者的装饰者类，注意它们实现了同一个接口：
``` java
// 定义装饰者
abstract class Decorator implements Human{
    // 该类持有接口对象
    private Human human;

    public Decorator(Human human) {
    	this.human = human;
    }

    @Override
    public void wearClothes() {
    	human.wearClothes();
    }

    @Override
    public void walkToWhere() {
    	human.walkToWhere();
    }
}	
```

下面定义三种装饰,功能依次细化,即装饰者的功能越来越多:
``` java
class Decorator_zero extends Decorator{
    public Decorator_zero(Human human) {
    	super(human);
    }

    public void goHome(){
    	System.out.println("进房子，找一件衣服穿穿");
    }

    public void findMap(){
    	System.out.println("拿起你的地图");
    }

    @Override
    public void wearClothes() {
    	super.wearClothes();
    	goHome();
    }
	
    @Override
    public void walkToWhere() {
    	super.walkToWhere();
    	findMap();
    }
}	
```

定义被装饰者，被装饰者有自己的初始状态与特征：
``` java
class Person implements Human{
    @Override
    public void walkToWhere() {
    	System.out.println("去哪里呢");
    }

    @Override
    public void wearClothes() {
    	System.out.println("穿什么呢");
    }
	}
```

测试类：
``` java
public class DecoratorTest {
    public static void main(String[] args) {
    	Human person = new Person();
    	Decorator decorator = new Decorator_zero(person);
    	decorator.walkToWhere();
    	decorator.wearClothes();
    }
}		
```

上面这个只是一个小例子，我们还可以一层层的继续装饰：

``` java
Decorator decorator = new Decorator_zero(Decorator_one(Decorator_two(person)));
```

一层一层的装饰它，给它添加新的功能。**装饰者模式**：动态的将责任附加到对象上。若要扩展功能，装饰者提供了比继承更有弹性的方案。
装饰者模式很简单，其在Java中应用，也有Java的IO装饰者，代码 Demo 如下：
``` java
// FilterInputStream 是所有InputStream的抽象装饰类。
class LowerCaseInputStream extends FilterInputStream{

    public LowerCaseInputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException{
        int c = super.read();
        return (c == -1?c:Character.toLowerCase((char)c));
    }

    public int read(byte[] b,int offest,int len) throws IOException{
        int result = super.read(b,offest,len);
        for (int i = offest;i < offest+result;i++){
            b[i] = (byte)Character.toLowerCase((char)b[i]);
        }
        return result;
    }
}
```

测试类如下所示：
``` java
public class InputTest {
    public static void main(String[] args) throws IOException {
        int c;
        try {
            InputStream in =
                    new LowerCaseInputStream(
                            new BufferedInputStream( //看这里包裹着各种修饰
                                    new FileInputStream("G:\\JavaTest\\src\\ttt.txt")));
            while((c = in.read())>=0){
                System.out.print((char)c);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
### 装饰者模式的优缺点

* 继承属于扩展形式之一，但是不是最佳的弹性设计方式，带来的问题也有许多。
* **组合和委托**可以在运行时动态的加上新的行为。
* 除了继承，装饰者模式也可以让我们扩展行为。
* 当然，装饰者会导致设计中出现许多小类，如果使用过度，程序会变得很复杂。