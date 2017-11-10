---
title: 设计模式之工厂模式
date: 2016-04-11 15:51:27
tags: design-pattern
categories: About Java
---

之前我们说过针对接口编程的好处，通过多态，可以让任何新类实现该接口。 new 之所以不好就是，当要大量使用具体类时，我们就要去修改，即想要新的具体类型来扩展代码，就必须重新打开。工厂模式就相当于创建实例对象的new，我们经常要根据类Class生成实例对象，如A a=new A(). 工厂模式也是用来创建实例对象的，可能多做一些工作，但会给你系统带来更大的可扩展性和尽量少的修改量。以类Sample为例,要创建Sample的实例对象:

``` java 
Sample sample=new Sample();
```

可是，实际情况是，通常我们都要在创建Sample实例时做点初始化的工作,比如赋值、查询数据库等。 首先，我们想到的是，可以使用Sample的构造函数，这样生成实例就写成:

``` java
Sample sample=new Sample(参数);
```

但是，如果创建sample实例时所做的初始化工作不是象赋值这样简单的事，可能是很长一段代码，如果也写入构造函数中，那你的代码很难看了初始化。工作如果是很长一段代码，说明要做的工作很多，将很多工作装入一个方法中，相当于将很多鸡蛋放在一个篮子里，是很危险的，这也是有背于Java面向对象的原则，面向对象的封装(Encapsulation)和分派(Delegation)告诉我们，尽量将长的代码分派“切割”成每段，将每段再“封装”起来(减少段和段之间偶合联系性)，这样，就会将风险分散，以后如果需要修改，只要更改每段，不会再发生牵一动百的事情。 我们需要将创建实例的工作与使用实例的工作分开, 也就是说，让创建实例所需要的大量初始化工作从Sample的构造函数中分离出去。你想如果有多个类似的类，我们就需要实例化出来多个类。这样代码管理起来就太复杂了。
这个时候你就可以采用工厂方法来封装这个问题。不能再用上面简单new Sample(参数)。还有,如果Sample有个继承，如MySample, 按照面向接口编程,我们需要将Sample抽象成一个接口.现在Sample是接口,有两个子类MySample 和HisSample 

``` java
Sample mysample=new MySample();
Sample hissample=new HisSample();
```

采用工厂封装：

``` java
public class Factory{
	public static Sample creator(int which){
	//getClass 产生Sample 一般可使用动态类装载装入类。
	if (which==1)
	return new SampleA();

	else if (which==2)
	return new SampleB();	
		}
	}
```
那么在你的程序中,如果要实例化Sample时.就使用

``` java
Sample sampleA= creator(1);
```

## 封装的应用

个人感觉这里所体现的思想还是较简单，只是将变化的部分封装成一个方法。这里你想要一个pizza，只需要交给orderPizza方法去生产就行了，你不需要管，pizzaStore也不需要去管。以书上披萨为例：

``` java
   Pizza orderPizza(String type) {
		Pizza pizza;
		// 经常变化，pizza的种类经常变化
		if(type.equals("cheese")){
			pizza = new CheesePizza();
		}else if(type.equals("clam")){
			pizza = new ClamPizza();
		}else if(type.equals("veggie")){
			pizza = new VeggiePizza();
		}
		// 这些操作都是不会变化的
		pizza.prepare();
		pizza.bake();
		pizza.cut();
		pizza.box();
	}
```

的确看着逻辑很清晰。但是一旦我们想要增加别的风味的 pizza ，而且不止增加一两个怎么办？直接在上面的代码中改动吗，增加if-else吗？不太好吧，前面还说着修改关闭。另外对于一个pizza，一些切、烘干、装箱操作都是不会变化的。为此引出**工厂方法**出现。


## 简单工厂模式

如下代码所示，将经常变化的代码与不变化的代码抽离开来，这样整体逻辑就更清晰一点：

``` java
	public class SimplePizzaFactory{
		public Pizza createPizza(String type){
			Pizza pizza = null;

			if(type.equals("cheese")){
				pizza = new CheesePizza();
			}else if(type.equals("clam")){
				pizza = new ClamPizza();
			}else if(type.equals("veggie")){
				pizza = new VeggiePizza();
			}
			return pizza;
		}
	}
```

而我们的客户代码，如下，仔细想想这样有什么好处? 解耦呗，不用去关心工厂怎么创建出pizza的。
``` java
	public class PizzaStore{
		SimplePizzaFactory factory;
		public PizzaStore(SimplePizzaFactory factory){
			this.factory = factory;
		}
		public Pizza orderPizza(String type){
			Pizza pizza;
			
			pizza = factory.createPizza(type);
			//不变化的代码拿出来
			pizza.prepare();
			pizza.bake();
			pizza.cut();
			pizza.box();
			return pizza;
		}
	}
```

接着回到一个简单的问题简单工厂和工厂方法之间有什么区别？
**简单工厂**把全部的事情在一个地方都出处理完了，而**工厂方法**在创建一个框架，让子类决定如何实现。比方说：在工厂方法中，orderPizza() 提供了一个一般的框架，以便创造出pizza，orderPizza() 依赖工厂方法创建具体类，并制造出实际的pizza。可通过集成pizzaStore类，来决定实际上制造出的pizza是什么。简单工厂的做法是，可以将对象的创建封装起来，但是简单工厂不具备工厂方法的弹性，**因为简单工厂不能变更在创建的产品**即，假如你要增加pizza的种类，你要去工厂里面修改，这样是很**麻烦**。


## 工厂模式

加盟店想提供不同口味的pizza，我们不方便有一个不同口味的加盟店就创建一种口味的pizza工厂。毕竟有许多过程是相同的，我们可以通过继承父类的方法，单独分离出来多个pizza工厂，每个工厂只能创建一中pizza，pizza总店还是抽象的，因为具体创建pizza 要交给**分店**去实现。

我们首先声明一个工厂方法，其为抽象的，让不同的子类去不同的实现：
``` java
	public abstracy class PizzaStore{
		
		public Pizza orderPizza(String type){
			Pizza pizza;
			pizza = create(type);
			pizza.prepare();
			pizza.bake();
			pizza.cut();
			pizza.box();
			return pizza;
		}
	 // 这个方法 负责实例化 Pizza，此方法就如同一个"工厂" 
     protected abstract Pizza createPizza(String pizza);	
    }
```

上面这个超类的 orderPizza() 并不知道正在创建的 Pizza() 是哪一种，它只知道，创建的 pizza，要被 bake()....
当然，必须有子类 Pizza 店，负责做出不同口味的 Pizza，并且子类 Pizza 点可以复用父类 Pizza 店处理订单的方法，也就是说，所有加盟店处理订单的方法都是一样的，有一个统一的规则。

具体的生产不同口味的pizza工厂：
```java
public class NYPizzaStore extends PizzaStore{
	Pizza createPizza(String item){
		if(item.equals("cheese")){
			return new NYStyleCheesePizza();
		}else if(){
			
		}else return null;
		}
}
```

当我们想要一个 NYstylePizza 时候，可以到 NYstylePizza 去购买：
```java
public class NYstyleCheesePizza extends Pizza{
	public NYstyleCheesePizza{
	// someting todo with pizza;	
	}
    //  override something with father Pizza；
}
```

当然，我们还需要有一个抽象 pizza 类，提供一些基本的属性和方法，各种风味的 pizza 都必须继承这个啊。
``` java
public abstract class Pizza{
	// something with pizza;	
}
```

最后顾客订餐啦：
```java
	PizzaStore nyStore = new NYstylestore();
	Pizza pizza = nystore.orderPizza("cheese");
	sout(pizza.getName());
```

总结就是：工厂方法模式通过让**子类**决定该创建的对象是什么，来达到将对象的创建过程封装的目的。**官方的说法就是**工厂方法模式定义了一个创建对象的接口，但是由子类决定要实例化的类是哪一个。即把类的实例化推迟到子类。


## 抽象工厂

为什么会有抽象工厂概念出来？ 如果我们对于 Pizza 什么原则或者模式都不用的话，各种口味的 Pizza 都要由 PizzaStore 来创建，这样Pizza的一些改变都会影响到 PizzaStore，我们就说 PizzaStore 依赖于 各种 Pizza。而显然，代码里减少对具体类的依赖是很重要的，而对此也出了一个设计原则：要依赖抽象，不要依赖具体实现。而对于上面这个原则，我们在抽象工厂中恰好应用到了。PizzaStore 依赖 Pizza ，而具体的 Pizza 类实现类了 Pizza 接口，故而也是依赖 Pizza。这种原则即**依赖倒置原则** 。下面我们用抽象工厂方法来创建 Pizza：


建造原料工厂的接口，Dough为一接口，每个原料都有对应的方法创建该原料。每个原料都是一个类：

``` java
public interface PizzaIngredientFactory {
    public Dough createDough();
    public Sauce createSauce();
	public Cheese createCheese();
	public Veggies[] createVeggies();
	public Pepperoni createPepperoni();
	public Clams createClam(); 
}
```

创建纽约原料工厂，实现了上述的接口：
``` java
public class NYPizzaIngredientFactory implements PizzaIngredientFactory {
	public Dough createDough() {
		return new ThinCrustDough();
	}
 
	public Sauce createSauce() {
		return new MarinaraSauce();
	}
 
	public Cheese createCheese() {
		return new ReggianoCheese();
	}
 
	public Veggies[] createVeggies() {
		Veggies veggies[] = { new Garlic(), new Onion(), new Mushroom(), new RedPepper() };
		return veggies;
	}
 
	public Pepperoni createPepperoni() {
		return new SlicedPepperoni();
	}

	public Clams createClam() {
		return new FreshClams();
		}
	}
```

纽约原料工厂的实现。重做 Pizza:
``` java
public abstract class Pizza {
	String name;
	Dough dough;
	Sauce sauce;
	Veggies veggies[];
	Cheese cheese;
	Pepperoni pepperoni;
	Clams clam;

	abstract void prepare();

	void bake() {
		System.out.println("Bake for 25 minutes at 350");
	}

	void cut() {
		System.out.println("Cutting the pizza into diagonal slices");
	}

	void box() {
		System.out.println("Place pizza in official PizzaStore box");
	}

	void setName(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	public String toString() {
	    //...
		return result.toString();
		}
	}
```
抽象 Pizza 类，定义一些通用的方法，比如对 Pizza 的包装等等

继续重做 Pizza，这次是中国味道的Pizza:
``` java
public class CheesePizza extends Pizza {
	PizzaIngredientFactory ingredientFactory;
 
	public CheesePizza(PizzaIngredientFactory ingredientFactory) {
		this.ingredientFactory = ingredientFactory;
	}
 
	void prepare() {
		System.out.println("Preparing " + name);
		dough = ingredientFactory.createDough();
		sauce = ingredientFactory.createSauce();
		cheese = ingredientFactory.createCheese();
	}
}
```

工厂提供原料来制作Pizza 。prepare中创建Pizza，每当创建 Pizza 需要原料时就跟工厂要。**注意**Pizza和区域原料之间被完全解耦，无论原料工厂在哪里，我们都不需要关心。

回到 Pizza 店铺

``` java
public abstract class PizzaStore {
	protected abstract Pizza createPizza(String item);
	public Pizza orderPizza(String type) {
		Pizza pizza = createPizza(type);
		System.out.println("--- Making a " + pizza.getName() + " ---");
		pizza.prepare();
		pizza.bake();
		pizza.cut();
		pizza.box();
		return pizza;
	}
}
```

纽约的Pizza店铺：
``` java
public class NYPizzaStore extends PizzaStore {
 
	protected Pizza createPizza(String item) {
		Pizza pizza = null;
		PizzaIngredientFactory ingredientFactory = 
			new NYPizzaIngredientFactory();
 
		if (item.equals("cheese")) {
  
			pizza = new CheesePizza(ingredientFactory);
			pizza.setName("New York Style Cheese Pizza");
  
		} else if (item.equals("veggie")) {
 
			pizza = new VeggiePizza(ingredientFactory);
			pizza.setName("New York Style Veggie Pizza");
 
		} else if (item.equals("clam")) {
 
			pizza = new ClamPizza(ingredientFactory);
			pizza.setName("New York Style Clam Pizza");
 
		} else if (item.equals("pepperoni")) {

			pizza = new PepperoniPizza(ingredientFactory);
			pizza.setName("New York Style Pepperoni Pizza");
 
		} 
		return pizza;
		}
}
```

客户订购 Pizza:
``` java
public class PizzaTestDrive {
 
	public static void main(String[] args) {
		PizzaStore nyStore = new NYPizzaStore();
		PizzaStore chicagoStore = new ChicagoPizzaStore();
 
		Pizza pizza = nyStore.orderPizza("cheese");
		System.out.println("Ethan ordered a " + pizza + "\n");
 
		pizza = chicagoStore.orderPizza("cheese");
		System.out.println("Joel ordered a " + pizza + "\n");
	}
}
```

最后 我们省略了各种原料实例，没有必要写了，new 一下 实例就出来了。

## 总结

* 所有的工厂都是用来封装对象的创建的
* 简单工厂虽然不是设计模式，但是仍然可以将程序从具体类中解耦。
* 工厂方法使用继承，将对象的创建委托给子类，子类实现工厂方法来创建对象。
* 抽象工厂使用对象组合，对象的创建被实现在工厂接口所暴露出来的方法中。
* 所有的工厂模式都是通过减少应用程序和具体类之间的依赖促进松耦合。
* 工厂促进我们针对抽象编程，而不是针对具体类来编程。
