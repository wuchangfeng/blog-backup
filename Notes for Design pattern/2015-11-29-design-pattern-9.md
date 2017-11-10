---
title: 设计模式之模板方法模式
date: 2016-04-27 21:10:09
tags: design-pattern
categories: About Java
---

模板方法体现了了一种延迟实现的思想。**模板方法模式**在一个方法中定义一个算法的骨架，而将一些步骤延迟到子类中。模板方法使得子类可以在不改变算法结构的情况下，重新定义算法的某些步骤。

### 实例引入

抽象基类，定义了模板方法即prepareRecipe：

``` java
public abstract class CaffeineBeverage {
	// 模板方法
	final void prepareRecipe() {
		boilWater();
		brew();
		pourInCup();
		addCondiments();
	}
    // 原语操作1
	abstract void brew();
	// 原语操作2
	abstract void addCondiments();
 
	void boilWater() {
		System.out.println("Boiling water");
	}
  
	void pourInCup() {
		System.out.println("Pouring into cup");
	}
}
```

具体实现类Coffee，实现上述的原语操作：
``` java
public class Coffee extends CaffeineBeverage {
	public void brew() {
		System.out.println("Dripping Coffee through filter");
		}
	public void addCondiments() {
		System.out.println("Adding Sugar and Milk");
		}	
}
```

具体实现类Tea，实现上述的原语操作：
``` java
public class Tea extends CaffeineBeverage {
	public void brew() {
		System.out.println("Steeping the tea");
		}
	public void addCondiments() {
		System.out.println("Adding Lemon");
		}
}
```

### 引入钩子

钩子是一种被声明在抽象类中的方法，但是只有空的或者默认的实现。**钩子的存在**可以让子类有能力对算法的不同点进行挂钩，要不要挂钩，由子类进行决定。customerWantsCondiments() 即称为钩子。

``` java
public abstract class CaffeineBeverageWithHook {
 
	void prepareRecipe() {
		boilWater();
		brew();
		pourInCup();
		if (customerWantsCondiments()) {
			addCondiments();
		}
	}
 
	abstract void brew();
 
	abstract void addCondiments();
 
	void boilWater() {
		System.out.println("Boiling water");
	}
 
	void pourInCup() {
		System.out.println("Pouring into cup");
	}

	boolean customerWantsCondiments() {
		return true;
		}
}
```

使用钩子：
``` java
public class CoffeeWithHook extends CaffeineBeverageWithHook {
	public void brew() {
		System.out.println("Dripping Coffee through filter");
	}
 
	public void addCondiments() {
		System.out.println("Adding Sugar and Milk");
	}
    //覆盖了父类的方法
	public boolean customerWantsCondiments() {

		String answer = getUserInput();

		if (answer.toLowerCase().startsWith("y")) {
			return true;
		} else {
			return false;
		}
	}
 
	private String getUserInput() {
		String answer = null;

		System.out.print("Would you like milk and sugar with your coffee (y/n)? ");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			answer = in.readLine();
		} catch (IOException ioe) {
			System.err.println("IO error trying to read your answer");
		}
		if (answer == null) {
			return "no";
		}
		return answer;
		}
}
```

测试类：
``` java
public class BeverageTestDrive {
	public static void main(String[] args) {
 
		Tea tea = new Tea();
		Coffee coffee = new Coffee();
 
		System.out.println("\nMaking tea...");
		tea.prepareRecipe();
 
		System.out.println("\nMaking coffee...");
		coffee.prepareRecipe();

 
		TeaWithHook teaHook = new TeaWithHook();
		CoffeeWithHook coffeeHook = new CoffeeWithHook();
        //prepareRecipe继承来自父类，子类直接调用即可，无需实现 
		System.out.println("\nMaking tea...");
		teaHook.prepareRecipe();
 
		System.out.println("\nMaking coffee...");
		coffeeHook.prepareRecipe();
	}
}
```

### 关于钩子的解释

* 钩子与抽象方法有什么区别

当子类必须实现某个方法或者步骤时，使用抽象方法。而钩子的话，是可选的，子类可以实现也可以不实现。

* 使用钩子方法真正的目的是什么

除了上面说的这一点。钩子另外一点作用就是让子类有机会对模板方法中某些即将发生的步骤做出反应。简单点说钩子可以让子类有能力为其抽象类做一些决定。


### JDK API中引入

用模板方法对鸭子进行排序，探究一下究竟哪里用到了模板方法

``` java
public class Duck implements Comparable {
	String name;
	int weight;
  
	public Duck(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}
 
	public String toString() {
		return name + " weighs " + weight;
	}
 
	public int compareTo(Object object) {
 
		Duck otherDuck = (Duck)object;
  
		if (this.weight < otherDuck.weight) {
			return -1;
		} else if (this.weight == otherDuck.weight) {
			return 0;
		} else { // this.weight > otherDuck.weight
			return 1;
			}
		}
}
```

测试类：
``` java
public class DuckSortTestDrive {

	public static void main(String[] args) {
		Duck[] ducks = { 
				new Duck("Daffy", 8), 
				new Duck("Dewey", 2),
				new Duck("Howard", 7),
				new Duck("Louie", 2),
				new Duck("Donald", 10), 
				new Duck("Huey", 2)
		 };

		System.out.println("Before sorting:");
		display(ducks);
        //Arrays类静态方法
		Arrays.sort(ducks);
 
		System.out.println("\nAfter sorting:");
		display(ducks);
	}

	public static void display(Duck[] ducks) {
		for (int i = 0; i < ducks.length; i++) {
			System.out.println(ducks[i]);
			}
		}
}
```

查看Java Api中的sort()方法，其中可以将**mergeSort()**看成模板方法，进一步compartTo() 由**子类或者别的类**来实现。
``` java
 public static void sort(Object[] a) {
        if (LegacyMergeSort.userRequested)
            legacyMergeSort(a);
        else
            ComparableTimSort.sort(a, 0, a.length, null, 0, 0);
    }

    /** To be removed in a future release. */
    private static void legacyMergeSort(Object[] a) {
        Object[] aux = a.clone();
        mergeSort(aux, a, 0, a.length, 0);
}
```

查看一下源码中mergeSort()中的实现：
``` java
private static void mergeSort(Object[] src,
                                  Object[] dest,
                                  int low,
                                  int high,
                                  int off) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (length < INSERTIONSORT_THRESHOLD) {
            for (int i=low; i<high; i++)
                for (int j=i; j>low &&
                         ((Comparable) dest[j-1]).compareTo(dest[j])>0; j--)
                    swap(dest, j, j-1);
            return;
        }

        // Recursively sort halves of dest into src
        int destLow  = low;
        int destHigh = high;
        low  += off;
        high += off;
        int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off);
        mergeSort(dest, src, mid, high, -off);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (((Comparable)src[mid-1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        // CompareTo() 具体的比较规则由子类去实现
        for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && ((Comparable)src[p]).compareTo(src[q])<=0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
}
```

正常的模板方法中，我们应该是用DuckSortTest去继承Arrays类，并实现其中的模板方法。而这个对鸭子排序的过程却并没有继承Arrays ，怎么去使用sort() 呢？

具体的原因就是sort()的设计者希望其能够适用于所有的数组，所以它被设计成静态方法。但是实际上sort()并不是真正的定义在超类中，所以sort()方法需要知道你实现了CompareTo()方法。而我们的**鸭子类恰好实现了 Comparable 接口**，并且实现了 其中定义的compareTo() 法。

``` java
public interface Comparable<T> {
        //实现类需要实现的方法
    	public int compareTo(T o);
}
```


### 模板模式的优缺点

* JDK 中 java.io 中的 InputStream 类有一个 read() 方法，是由其子类实现的，而这个方法又会被 read(byte b[],int off,int len)模板方法使用。
* sort() 模板方法的实现决定不使用继承，sort 方法被实现成一个静态的方法，在运行时和 Compareable 组合。
* 为了防止子类改变模板方法中的算法，可以将模板方法声明为 final。
* 策略模式和模板方法模式都是在封装算法,一个用组合一个用继承。
* 由于策略模式开始学的，导致不是太明白，抽时间看一下。