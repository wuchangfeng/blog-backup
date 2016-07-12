---
title: 迭代器模式
date: 2016-05-01 20:35:59
tags: design-pattern
categories: About Java
---

Head first 设计模式-迭代器模式

1. 迭代器模式允许访问集合中的元素，而不暴露其内部的结构，即其内部到底存储方式是数组还是 List

2. 迭代器模式将遍历结合的工作封装进一个对象中

3. 迭代器模式提供了一个通用的接口

<!-- more -->

### 定义

提供一种方法，顺序访问一个集合对象中的各个元素，但是又不暴露其内部元素的表示。这样让集合的接口接口和实现变得简单，也可以让集合更加专注于管理集合中的元素，而不用花费精力去理会遍历上的事情。

### 引入

对象村餐厅和对象村煎饼屋合并了，意味着可以在同一地方点餐了。但是 Lou 和 Mel 在菜单的实现上却采用了不同的数据结构。

Lou 煎饼屋菜单实现(ArrayList)

``` java
public class PancakeHouseMenu implements Menu {
	ArrayList menuItems;
 
	public PancakeHouseMenu() {
		menuItems = new ArrayList();
    
		addItem("K&B's Pancake Breakfast", 
			"Pancakes with scrambled eggs, and toast", 
			true,
			2.99);
 
		addItem("Regular Pancake Breakfast", 
			"Pancakes with fried eggs, sausage", 
			false,
			2.99);
 
		addItem("Blueberry Pancakes",
			"Pancakes made with fresh blueberries",
			true,
			3.49);
 
		addItem("Waffles",
			"Waffles, with your choice of blueberries or strawberries",
			true,
			3.59);
	}

	public void addItem(String name, String description,
	                    boolean vegetarian, double price)
	{
		MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
		menuItems.add(menuItem);
	}
 
	public ArrayList getMenuItems() {
		return menuItems;
	}

	// other menu methods here depends on arraylist
}
```

Mel 的餐厅菜单实现(数组)

``` java
public class DinerMenu implements Menu {
	static final int MAX_ITEMS = 6;
	int numberOfItems = 0;
	MenuItem[] menuItems;
  
	public DinerMenu() {
		menuItems = new MenuItem[MAX_ITEMS];
 
		addItem("Vegetarian BLT",
			"(Fakin') Bacon with lettuce & tomato on whole wheat", true, 2.99);
		addItem("BLT",
			"Bacon with lettuce & tomato on whole wheat", false, 2.99);
		addItem("Soup of the day",
			"Soup of the day, with a side of potato salad", false, 3.29);
		addItem("Hotdog",
			"A hot dog, with saurkraut, relish, onions, topped with cheese",
			false, 3.05);
		addItem("Steamed Veggies and Brown Rice",
			"Steamed vegetables over brown rice", true, 3.99);
		addItem("Pasta",
			"Spaghetti with Marinara Sauce, and a slice of sourdough bread",
			true, 3.89);
	}
  
	public void addItem(String name, String description, 
	                     boolean vegetarian, double price) 
	{
		MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
		if (numberOfItems >= MAX_ITEMS) {
			System.err.println("Sorry, menu is full!  Can't add item to menu");
		} else {
			menuItems[numberOfItems] = menuItem;
			numberOfItems = numberOfItems + 1;
		}
	}
 
	public MenuItem[] getMenuItems() {
		return menuItems;
	}

	// other menu methods here
}
```

不同的菜单表现形式会导致的问题：由于菜单项采用不同的表现形式，如果想要遍历菜单项内容需要采用不同的遍历方法，更进一步以下问题也是存在的：

* 针对的是 PancakeHouseMenu 和 DinerMenu 的具体实现编码，而不是针对接口编程
* 如果决定从 DinnerMenu 切换到另一种菜单，此菜单的项是用 Hashtable 来存放的，我们因此也会修改许多女招待中的代码
* 女招待需要知道每个菜单如何表单内部的菜单项集合，违反了封装
* 如果有第三种存放菜单的方式，我们还是需要另建一个循环来遍历菜单

Mel 和 Lou 都不愿意改变自己的实现结构，现在就想出能不能有一种办法适应不同数据结构存储的菜单项的遍历，很明显这是由于不同集合的类型所造成的问题，能够封装这一部分，即封装遍历？

### 解决

#### 定义迭代器接口：

``` java
public interface Iterator {
	boolean hasNext();
	Object next();
}
```

#### 实现具体的迭代器为餐厅菜单服务：

具体的操作还是针对数组，而煎饼屋针对的是 ArrayList 实现也是不一样的

``` java
public class DinerMenuIterator implements Iterator {
	MenuItem[] items;
	int position = 0;
 
	public DinerMenuIterator(MenuItem[] items) {
		this.items = items;
	}
 
	public Object next() {
		MenuItem menuItem = items[position];
		position = position + 1;
		return menuItem;
	}
 
	public boolean hasNext() {
		if (position >= items.length || items[position] == null) {
			return false;
		} else {
			return true;
		}
	}
}

```

#### 用迭代器改写餐厅菜单：

``` java
public class DinerMenu implements Menu {
	static final int MAX_ITEMS = 6;
	int numberOfItems = 0;
	MenuItem[] menuItems;
  
	public DinerMenu() {
		menuItems = new MenuItem[MAX_ITEMS];
 
		addItem("Vegetarian BLT",
			"(Fakin') Bacon with lettuce & tomato on whole wheat", true, 2.99);
		addItem("BLT",
			"Bacon with lettuce & tomato on whole wheat", false, 2.99);
		addItem("Soup of the day",
			"Soup of the day, with a side of potato salad", false, 3.29);
		addItem("Hotdog",
			"A hot dog, with saurkraut, relish, onions, topped with cheese",
			false, 3.05);
		addItem("Steamed Veggies and Brown Rice",
			"Steamed vegetables over brown rice", true, 3.99);
		addItem("Pasta",
			"Spaghetti with Marinara Sauce, and a slice of sourdough bread",
			true, 3.89);
	}
  
	public void addItem(String name, String description, 
	                     boolean vegetarian, double price) 
	{
		MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
		if (numberOfItems >= MAX_ITEMS) {
			System.err.println("Sorry, menu is full!  Can't add item to menu");
		} else {
			menuItems[numberOfItems] = menuItem;
			numberOfItems = numberOfItems + 1;
		}
	}
 
	public MenuItem[] getMenuItems() {
		return menuItems;
	}
  
	public Iterator createIterator() {
		return new DinerMenuIterator(menuItems);
	}
 
	// other menu methods here
}
```

上述代码中改变最大的就是删除了 getMenuItems() ，事实上这个方法会暴露我们的内部实现。

#### 修改女招待的代码：

``` java
public class Waitress {
	PancakeHouseMenu pancakeHouseMenu;
	DinerMenu dinerMenu;
 
	public Waitress(PancakeHouseMenu pancakeHouseMenu, DinerMenu dinerMenu) {
		this.pancakeHouseMenu = pancakeHouseMenu;
		this.dinerMenu = dinerMenu;
	}
 
	public void printMenu() {
		Iterator pancakeIterator = pancakeHouseMenu.createIterator();
		Iterator dinerIterator = dinerMenu.createIterator();

		System.out.println("MENU\n----\nBREAKFAST");
		printMenu(pancakeIterator);
		System.out.println("\nLUNCH");
		printMenu(dinerIterator);
	}
    //只需要一个循环
	private void printMenu(Iterator iterator) {
		while (iterator.hasNext()) {
			MenuItem menuItem = (MenuItem)iterator.next();
			System.out.print(menuItem.getName() + ", ");
			System.out.print(menuItem.getPrice() + " -- ");
			System.out.println(menuItem.getDescription());
		}
	}
}
```

#### 测试程序：

``` java
public class MenuTestDriver{
	public static void main(String args[]){

		PancakeHouseMenu phm = new PancakeHouseMenu();
		DinerMenu dm = new DinerMenu();
		Waitress waitress = new Waitress(phm,dm);
		
		waitress.printMenu();
	}
}
```
#### 解决的问题：

* 菜单的实现已经被封装起来了，女招待不知道内部是如何存储菜单集合的

* 只需要实现迭代器，只需要一个集合就能多态的处理任何项的集合

* 之前女招待捆绑于具体类(MenuItem[]和Arraylist)

但是女招待仍然捆绑于两个具体的菜单类，尽管他们的接口差不多是一致的。

### 优化

#### 使用 Java 提供的迭代器接口

我们大可以不必自定义迭代器接口，引入如下代码：

``` java
public Iterator createIterator(){
	return menuItems.iterator();	
}
```
接着在 DinerMenu 中：

``` java
import java.util.Iterator

public class DinerMenuIterator implements Iterator{
  //....
}
```
紧接着我们给菜单提供一个公共的接口，里面可以加上 addItem(),但是目前还是让厨师控制他们的菜单，不把那些放在公开接口中：

``` java
public interface Menu{
	public Iterator createIterator();
}
```

让煎饼屋菜单类和餐厅菜单类都实现 Menu接口，然后更新女侍代码如下：

``` java
import java.util.Iterator;
   
public class Waitress {
	//菜单接口
	Menu pancakeHouseMenu;
	Menu dinerMenu;
 
	public Waitress(Menu pancakeHouseMenu, Menu dinerMenu) {
		this.pancakeHouseMenu = pancakeHouseMenu;
		this.dinerMenu = dinerMenu;
	}
 
	public void printMenu() {
		Iterator pancakeIterator = pancakeHouseMenu.createIterator();
		Iterator dinerIterator = dinerMenu.createIterator();

		System.out.println("MENU\n----\nBREAKFAST");
		printMenu(pancakeIterator);
		System.out.println("\nLUNCH");
		printMenu(dinerIterator);
	}
 
	private void printMenu(Iterator iterator) {
		while (iterator.hasNext()) {
			MenuItem menuItem = (MenuItem)iterator.next();
			System.out.print(menuItem.getName() + ", ");
			System.out.print(menuItem.getPrice() + " -- ");
			System.out.println(menuItem.getDescription());
		}
	}
 }
```
如上，女招待可以利用**接口而不是具体类**来引用每一个菜单对象，这又是针对接口编程的体现，减少对具体类的依赖。

优化过后，女招待只关心菜单和迭代器这两个接口。


