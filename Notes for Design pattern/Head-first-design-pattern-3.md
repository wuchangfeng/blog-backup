---
title: 装饰者模式
date: 2016-04-05 19:19:02
tags: design-pattern
categories: About Java
---

Head first 设计模式-装饰者模式

谈到装饰者模式就要与继承扯上一点关系，想想继承的不好。由此我们可以带来一种对象组合的技巧，在运行时动态的装饰类，一旦你熟悉了装饰的技巧，就能够在不修改任何**底层代码**的情况下，给你的(或别人)对象赋予新的职责。

<!--more-->

### 一 . 引入

底下这个实例感觉非常简单，可以更好理解。

[简单实例](http://blog.csdn.net/jason0539/article/details/22713711)

一句话，运行时扩展的威力比编译时扩展的威力大，编译时扩展就是利用继承。我们可以利用装饰的技巧在不修改任何底层代码的情况下，来给人家赋予新的功能。

另外设计的初衷，一般是用继承来扩充父类的功能，但是子类如果种类很多，那么就会有很多类出来，增加了系统的复杂性，同时,使用继承实现功能拓展,我们必须可预见这些拓展功能,这些功能是编译时就确定了,**是静态的**。最后一句话是抄来的，不能理解，静态带来的坏处？

> 静态的东西就是编译期间预先加载到内存，如果你一个大程序，所有都用静态，你想，当你一运行该程序，你看看内存，占用率极高，那么性能就会降低。

上面是百度过来的，很有道理昂。


### 二 . 实例

一个简单实例实现，非常简单，参考的就是上面的连接，代码如下：

``` java
// 定义被装饰者
interface Human{

    	public void wearClothes();
    	public void walkToWhere();
}

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

// 下面定义三种装饰,功能依次细化,即装饰者的功能越来越多
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

// 定义被装饰者，被装饰者有自己的初始状态
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

	Decorator decorator = new Decorator_zero(Decorator_one(Decorator_two(person)));

一层一层的装饰它，给它添加新的功能。

**装饰者模式**：动态的将责任附加到对象上。若要扩展功能，装饰者提供了比继承更有弹性的方案。


装饰者模式很简单，其在 java 中应用，也有 java 的 i/o 装饰者，代码 Demo 如下：

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

//测试类
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
### 三 . 总结

* 继承属于扩展形式之一，但是不是最佳的弹性设计方式，带来的问题也有许多。
* **组合和委托**可以在运行时动态的加上新的行为。
* 除了继承，装饰者模式也可以让我们扩展行为。
* 当然，装饰者会导致设计中出现许多小类，如果使用过度，程序会变得很复杂。