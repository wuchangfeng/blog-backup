---
title: 设计模式之组合模式
date: 2016-05-07 16:21:59
tags: design-pattern
categories: About Java
---

组合模式，将对象组合成树形结构以表示“部分-整体”的层次结构，组合模式使得用户对单个对象和组合对象的使用具有一致性。
文件系统以及 Android 中的 View 以及 ViewGroup 就是这样的结构。

### 定义引入

有时候又叫做部分-整体模式，它使我们树型结构的问题中，模糊了简单元素和复杂元素的概念，客户程序可以像处理简单元素一样来处理复杂元素,从而使得客户程序与复杂元素的内部结构解耦。
组合模式让你可以优化处理递归或分级数据结构。有许多关于分级数据结构的例子，使得组合模式非常有用武之地。关于分级数据结构的一个普遍性的例子是你每次使用电脑时所遇到的:文件系统。文件系统由目录和文件组成。每个目录都可以装内容。目录的内容可以是文件，也可以是目录。按照这种方式，计算机的文件系统就是以递归结构来组织的。如果你想要描述这样的数据结构，那么你可以使用组合模式Composite。


###  结构组成

1. Component 是组合中的对象声明接口，在适当的情况下，实现所有类共有接口的默认行为。声明一个接口用于访问和管理Component子部件。
2. Leaf 在组合中表示叶子结点对象，叶子结点没有子结点。
3. Composite 定义有枝节点行为，用来存储子部件，在Component接口中实现与子部件有关操作，如增加(add)和删除(remove)等。

其中 叶子对象和组合对象实现相同的接口，以此叶子节点和组合节点能够被一致性进行处理。

组合模式在文件系统中的应用就像下面这张图一样，对于用户而言，都是在浏览文件，不存在什么差别。

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E7%BB%84%E5%90%88%E6%A8%A1%E5%BC%8F.jpg)

### 需求引出

餐厅要创建一份甜点菜单，并将它放入常规的菜单中，即要支持菜单中的菜单。

所以，在新的设计中，我们需要：

1. 某种树形结构，可以容纳菜单、子菜单和菜单项；

2. 需要确定能够在每个菜单的各个项之间游走，并且要像现在使用迭代器一样方便；

3. 需要能够更有弹性地在菜单项之间游走。比方说，可能只需要遍历甜点菜单，或者可以遍历餐厅的整个菜单（包括甜点菜单）。

这里就要引入组合模式。

### 实例类图

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E7%BB%84%E5%90%88%E6%A8%A1%E5%BC%8F%E5%AE%9E%E4%BE%8B2.jpg)

### 实例代码

1. 实现菜单组件抽象类,其为叶节点和组合节点共同接口

``` java
public abstract class MenuComponent {
   
	public void add(MenuComponent menuComponent) {
		throw new UnsupportedOperationException();
	}
	public void remove(MenuComponent menuComponent) {
		throw new UnsupportedOperationException();
	}
	public MenuComponent getChild(int i) {
		throw new UnsupportedOperationException();
	}
  	// 底下这些方法可以被叶子和组合共同使用
	public String getName() {
		throw new UnsupportedOperationException();
	}
	public String getDescription() {
		throw new UnsupportedOperationException();
	}
	public double getPrice() {
		throw new UnsupportedOperationException();
	}
	public boolean isVegetarian() {
		throw new UnsupportedOperationException();
	}

	public abstract Iterator createIterator();
 
	public void print() {
		throw new UnsupportedOperationException();
	}
}
```

2. 实现菜单项，即组合类图中的叶子类

``` java
public class MenuItem extends MenuComponent {
 
	String name;
	String description;
	boolean vegetarian;
	double price;
    
	public MenuItem(String name, 
	                String description, 
	                boolean vegetarian, 
	                double price) 
	{ 
		this.name = name;
		this.description = description;
		this.vegetarian = vegetarian;
		this.price = price;
	}
  
	public String getName() {
		return name;
	}
  
	public String getDescription() {
		return description;
	}
  
	public double getPrice() {
		return price;
	}
  
	public boolean isVegetarian() {
		return vegetarian;
	}

	public Iterator createIterator() {
		return new NullIterator();
	}
 	// 覆盖了父类 print(),对于菜单项来说，打印出具体的菜单条目
	public void print() {
		System.out.print("  " + getName());
		if (isVegetarian()) {
			System.out.print("(v)");
		}
		System.out.println(", " + getPrice());
		System.out.println("     -- " + getDescription());
	}
}
```

3. 实现组合菜单，即组合类，其持有组合和菜单项，要注意该类中的 print() 方法

``` java
public class Menu extends MenuComponent {
 	// 用来记录菜单的孩子
	ArrayList menuComponents = new ArrayList();
	String name;
	String description;
  
	public Menu(String name, String description) {
		this.name = name;
		this.description = description;
	}
 
	public void add(MenuComponent menuComponent) {
		menuComponents.add(menuComponent);
	}
 
	public void remove(MenuComponent menuComponent) {
		menuComponents.remove(menuComponent);
	}
 
	public MenuComponent getChild(int i) {
		return (MenuComponent)menuComponents.get(i);
	}
 
	public String getName() {
		return name;
	}
 
	public String getDescription() {
		return description;
	}

  
	public Iterator createIterator() {
		return new CompositeIterator(menuComponents.iterator());
	}
 
 	// 非常巧妙的利用递归自己打印自己,一旦遇到菜单类的话
	public void print() {
		System.out.print("\n" + getName());
		System.out.println(", " + getDescription());
		System.out.println("---------------------");
  
		Iterator iterator = menuComponents.iterator();
		while (iterator.hasNext()) {
			MenuComponent menuComponent = 
				(MenuComponent)iterator.next();
			menuComponent.print();
		}
	}
}
```

4. 测试程序

``` java
public class MenuTestDrive {
	public static void main(String args[]) {
		
		// 创建所有菜单
		MenuComponent pancakeHouseMenu = 
			new Menu("PANCAKE HOUSE MENU", "Breakfast");
		MenuComponent dinerMenu = 
			new Menu("DINER MENU", "Lunch");
		MenuComponent cafeMenu = 
			new Menu("CAFE MENU", "Dinner");
		MenuComponent dessertMenu = 
			new Menu("DESSERT MENU", "Dessert of course!");

  		// 创建顶级菜单
		MenuComponent allMenus = new Menu("ALL MENUS", "All menus combined");
  
		allMenus.add(pancakeHouseMenu);
		allMenus.add(dinerMenu);
		allMenus.add(cafeMenu);
  		
		// 构造菜单项
		pancakeHouseMenu.add(new MenuItem(
			"K&B's Pancake Breakfast", 
			"Pancakes with scrambled eggs, and toast", 
			true,
			2.99));
		pancakeHouseMenu.add(new MenuItem(
			"Regular Pancake Breakfast", 
			"Pancakes with fried eggs, sausage", 
			false,
			2.99));
		pancakeHouseMenu.add(new MenuItem(
			"Blueberry Pancakes",
			"Pancakes made with fresh blueberries, and blueberry syrup",
			true,
			3.49));
		pancakeHouseMenu.add(new MenuItem(
			"Waffles",
			"Waffles, with your choice of blueberries or strawberries",
			true,
			3.59));

		dinerMenu.add(new MenuItem(
			"Vegetarian BLT",
			"(Fakin') Bacon with lettuce & tomato on whole wheat", 
			true, 
			2.99));
		dinerMenu.add(new MenuItem(
			"BLT",
			"Bacon with lettuce & tomato on whole wheat", 
			false, 
			2.99));
		dinerMenu.add(new MenuItem(
			"Soup of the day",
			"A bowl of the soup of the day, with a side of potato salad", 
			false, 
			3.29));
		dinerMenu.add(new MenuItem(
			"Hotdog",
			"A hot dog, with saurkraut, relish, onions, topped with cheese",
			false, 
			3.05));
		dinerMenu.add(new MenuItem(
			"Steamed Veggies and Brown Rice",
			"A medly of steamed vegetables over brown rice", 
			true, 
			3.99));
 
		dinerMenu.add(new MenuItem(
			"Pasta",
			"Spaghetti with Marinara Sauce, and a slice of sourdough bread",
			true, 
			3.89));
   
		dinerMenu.add(dessertMenu);
  
		dessertMenu.add(new MenuItem(
			"Apple Pie",
			"Apple pie with a flakey crust, topped with vanilla icecream",
			true,
			1.59));
		dessertMenu.add(new MenuItem(
			"Cheesecake",
			"Creamy New York cheesecake, with a chocolate graham crust",
			true,
			1.99));
		dessertMenu.add(new MenuItem(
			"Sorbet",
			"A scoop of raspberry and a scoop of lime",
			true,
			1.89));

		cafeMenu.add(new MenuItem(
			"Veggie Burger and Air Fries",
			"Veggie burger on a whole wheat bun, lettuce, tomato, and fries",
			true, 
			3.99));
		cafeMenu.add(new MenuItem(
			"Soup of the day",
			"A cup of the soup of the day, with a side salad",
			false, 
			3.69));
		cafeMenu.add(new MenuItem(
			"Burrito",
			"A large burrito, with whole pinto beans, salsa, guacamole",
			true, 
			4.29));
 		
		// 菜单层次构造完毕，交给服务员
		Waitress waitress = new Waitress(allMenus);
   
		waitress.printVegetarianMenu();
 
	}
}
```

4. 女招待的实现，其实我们很容易就简单的实现出 女招待的代码，但是我们仍然想给她附加一些权力，我们也能让她使用迭代器遍历整个组合，挑出其中的素食菜单。那就要想着去实现一个 **组合迭代器了**，为每个组件都加上 createIterator() 方法。
当然组合迭代器始终是一个外部迭代器，所以要有许多要追踪的事情，迭代器必须维护她在遍历中的位置，以便外部客户能够通过调用 hasNext() 和 next() 来驱动遍历，这就是为什么我们会采用堆栈来维护我们的位置。

下面这段代码的作用就是遍历组件内的菜单项，而且确保所有的子菜单都被包括进来。

``` java
public class CompositeIterator implements Iterator {
	Stack stack = new Stack();

	// 顶层组合的迭代器
	public CompositeIterator(Iterator iterator) {
		stack.push(iterator);
	}


	public Object next() {
		// use hasNext() judge 
		if (hasNext()) {
			// if true,get the current iterator from stack
			Iterator iterator = (Iterator) stack.peek();
			// get the next element ,it is menu,not iterator
			MenuComponent component = (MenuComponent) iterator.next();
			// if it was menu  put it into the stack
			if (component instanceof Menu) {
				// push stack
				stack.push(component.createIterator());
			}
			// return component weather menu or not
			return component;
		} else {
			return null;
		}
	}
  
	public boolean hasNext() {
		if (stack.empty()) {
			return false;
		} else {
			Iterator iterator = (Iterator) stack.peek();
			if (!iterator.hasNext()) {
				stack.pop();
				return hasNext();
			} else {
				return true;
			}
		}
	}
   
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
```

涉及到迭代器，感觉有点混乱，注意点：迭代器是一种设计模式，有种绑定集合，但是不用知道集合内部怎么实现的感觉。就像这里的压栈进去的 iterator 其关联的是一些菜单。

