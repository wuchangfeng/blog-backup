---
title: Java 常用知识点总结1
date: 2015-08-31 20:38:02
tags: java
---

java 常用琐碎知识点1

<!-- more -->

### 1. 堆内存和栈内存

**[垃圾回收算法](http://gold.xitu.io/entry/56fb6b3a5bbb5000497bef00)**

为什么会有堆内存和栈内存之分？

Java把内存划分成两种：一种是栈内存，一种是堆内存。

栈内存
 
在函数中定义的一些**基本类型的变量和对象的引用变量**都在函数的栈内存中分配。 

当在一段代码块定义一个变量时，Java就在栈中为这个变量分配内存空间，当**超过变量的作用域**后以及方法的结束，Java会**自动**释放掉为该变量所分配的内存空间，该内存空间可以立即被另作他用。 

堆内存

堆内存用来存放由**new创建的对象和数组**。 

在程序中创建一个对象时，被保存在运行时数据区中，以便反复利用因为创建一个对象的成本很高。
在堆中分配的内存，由Java虚拟机的自动垃圾回收器来管理。 

在堆中产生了一个数组或对象后，还可以在栈中定义一个特殊的变量，让栈中这个变量的取值等于数组或对象在堆内存中的首地址，栈中的这个变量就成了数组或对象的引用变量。 

引用变量就相当于是为数组或对象起的一个名称，以后就可以在程序中使用栈中的引用变量来访问堆中的数组或对象。 

### 2. java 运行机制

* 高级语言的运行机制

计算机高级语言按照程序的执行方式可以分为编译型和解释型两种。

编译型语言是指专门的编译器,针对指定的平台(操作系统)将某种高级语言源代码一次性"翻译"成该平台硬件可执行的机器码，这个转换过程称为编译。编译生成的可执行的程序可以脱离开发环境，在特定的平台下独立运行。

解释性语言是指使用专门的解释器对源程序逐行解释成特定平台的机器码并立即执行的语言。解释性语言通常不会进行整体性的编译和链接处理。

可以认为：每次执行解释性语言的程序都需要进行一次编译，因此解释性语言的程序运行效率慢，不能脱离解释器独立运行。但是解释性语言有其优势：跨平台比较容易。别的平台只需要提供相应解释器。

而Java语言比较奇葩：Java语言都要经过编译过程，但是并不会生成特定平台的机器码，而生成一种与平台无关的字节码(*.class文件)。当然，这种字节码不是可执行的，必须使用Java解释器来完成。因此Java程序必须经过先编译，后解释两个步骤。

由此我们引出了JVM的概念。

### 3. 垃圾回收机制

与C/C++不同的是，Java程序的内存分配和回收都是由JRE在后台自动进行的。即GC机制。JRE会提供一个后台线程来进行检测和监控，一般都是在CPU或者内存不足时，进行GC。

#### good:

 * 很好的提高编程效率，没有GC的话，需要花费时间来弄懂存储问题。
 * 保护程序的完整性。
#### bad:

 * jvm 必须跟踪程序中有用的对象，才可以确定哪些是无用的对象，并最终释放这些无用的对象，这个过程需要花费处理器时间。
 * 垃圾算法回收的不完整性。
### sum

任何一种垃圾回收算法，做两件事：发现无用的对象；回收被占用的空间。

 * 回收的是堆内存，其他物理资源，如：数据库连接，磁盘I/o 则无能为力。
 * 为了更快让垃圾回收机制回收那些无用的对象，可以将该对象的引用变量设置为null。
 * 垃圾回收的不可预知性。
 * 垃圾回收的精确性。

#### 4. 对象的垃圾回收

概念

* 当程序**创建对象，数组**等**引用类型实体**时，系统都会在**堆内存**中为之分配一块内存区，对象就保存在这块内存区，当这块内存不再被任何变量引用时，这块内存就变成垃圾，系统就要回收。

 * 只回收堆内存中对象，不会回收物理资源
 * 程序无法精确控制回收时机。
 * 在垃圾回收机制回收任何对象之前，总会先调用它的finlize()方法，可能导致**垃圾回收机制取消**。即要死的对象复活


* 对象在内存中的状态
 * 可达状态：对象被创建之后，一个以上变量引用它。
 * 可恢复状态：没有对象引用它。在系统调用 finalize() 方法清理资源时，让引用重新指向，则其变为可达状态。
 * 不可达状态 ： 顾名思义。

* 强制垃圾回收

当一个对象失去引用之后，系统何时调用它的 finalize() 方法对其进行清理。何时会变成不可达状态，何时回收其占内存，对于程序完全透明，程序只能控制一个对象何时不被任何引用变量引用，但是不能控制它何时被回收。

简单点说就是程序无法控制垃圾回收时机，但是程序可以强制系统进行垃圾回收，注意，**只是**通知而已，方法：

	System.gc()
	Runtime.getRuntime().gc()


实例：

	public class GcTest {
    public static void main(String args[]){

        for (int i = 0;i < 4;i ++){

            new GcTest();

            //System.gc();

            Runtime.getRuntime().gc();
        }
    }

    public void finalize(){

        System.out.println("系统在进行垃圾回收");
    }

	}

注意结果依然具有不确定性。

finalize方法

Java提供的默认机制来清理对象资源，即 finalize() 方法，垃圾回收机制何时调用该对象的 finalize() 方法完全是透明的，只有程序认为需要额外内存时，垃圾回收机制才会进行回收。

p227
   
### 5. 对象的软，弱和虚引用

大部分对象而言，会有一个引用变量引用该对象，其常见。除此之外，在java.lang.ref包下提供了3个类：softReference,PhantomReference和weakReference。分别代表三种：软，弱，虚三种引用，因此有四种引用对象的方式：

* 强引用

这种常见,当一个对象被一个或者一个以上的引用变量所引用时，它处于可大状态，不可能被系统垃圾回收机制回收。

* 软引用

通常用于内存敏感的程序中，这样应该知道什么意思吧？


* 弱引用

只要进行垃圾回收机制，肯定首先被进行垃圾回收。不管内存是否足够。

* 虚引用

其等同于没有引用。其主要用于跟踪对象被垃圾回收的状态，虚引用不能单独使用，虚引用必须和引用队列联合引用。




### 6.Super 关键字

用于在子类中，调用父类中被子类覆盖的方法。

### 7. 对象，指针和引用

Person p = new Person(),这里的p可以成为**引用**，相当于C语言中的指针。对象的成员变量数据实际放在堆内存中，而引用变量存储在栈中，指向该堆里的对象。

因此如果堆内存中的对象没有被任何引用变量指向，那么这个对象也就成为了垃圾，Java内存回收机制将回收该对象，释放其所占的内存区。

### 8. 再看**多态**即多态原理

Java 引用变量有两种类型，一个是编译时类型，一个是运行时类型。编译时类型由申明该变量使用的类型决定。运行时类型由实际赋给该变量的类型决定。如果编译时类型和运行时类型不一致，就有可能出现多态。

	

	class BaseClass{
    	public int book = 6;
    	public void base(){
    	    System.out.println("父类的普通方法");
    	}
    	public void test(){
        	System.out.println("父类的被覆盖的方法");
    	}
	}


	public class SubClass extends BaseClass {

    public String book = "book1";
    public void sub(){
        System.out.println("子类的普通方法");
    }

    @Override
    public void test() {
       // super.test();
        System.out.println("子类覆盖的父类的方法");
    }

    public static void main(String args[]){

        BaseClass bc = new BaseClass();
        System.out.println(bc.book);
        bc.base();
        bc.test();
        
        SubClass sc = new SubClass();
        System.out.println(sc.book);
        sc.base();
        sc.test();

        BaseClass bc2 = new SubClass();
        System.out.println(bc2.book);
        bc2.base();
        bc2.test();
        //BaseClass 类没有提供sub()方法，所以下面的代码会在编译时出错
        //bc2.sub();
	    }
	}

解释：当把一个子类对象直接赋值给父类引用变量时，例如上面的 BaseClass bc2 = new SubClass();这个bc2引用变量的编译时类型是BaseClass，而运行时类型是SubClass，当运行时调用该引用变量的方法时，其方法总是表现出**子类的方法行为特征**，而不是父类的行为特征，这就可能出现：相同类型的变量，调用同一个方法时呈现出多种不同的行为特征，这就是多态。

上面注释了bc2.class ，这段代码会在编译时发生错误，虽然bc2引用变量确实包含sub()方法（可以通过反射来执行该方法），但因为它的编译时类型为BaseClass，因此编译时无法调用sub()方法。

当把子类对象赋给父类引用变量时，被称为向上转型。这种转型总是能够成功的。这种转型只是表明这个引用变量编译时类型是父类，但是实际执行它的方法时，仍然表现出子类对象的行为方式。但是把一个子类对象赋给子类引用变量时，就需要进行强制类型转换，而且可能运行时产生ClassCastException异常，使用instanceof运算符可以让强制类型转换更加安全。

instanceof作用是判断一个对象是否是一个类的实例。



### 9. 利用组合实现复用

需要复用一个类，除了继承，还可以用组合。组合是把旧类对象作为新类的成员变量组合进来，用来实现新类的功能，通常用private修饰这个旧类对象。

继承本质上来讲，还是对已有的方法进行一些改造。
	
	Class Animal{
		private void beat(){
			Sys.out("心脏跳动")；
		}
		public void breath(){
			beat();
			Sys.out("吸一口气，呼吸中");
		}
	}

	Class Bird{
		private Animal a;
		public Bird(Animal a){
			this.a = a;
		}
		public void breath(){
			a.breath();
		}
		private void fly(){
			Sys.out("飞")；
		}

	}

	public class CompiteTest{
		public static void main(){
			Animal a = new Animal();
			Bird b = new Bird(al);
			b.breath();
			b.fly();
		}
	}

### 10. 初始化快

能说清楚为什么需要初始化块吗？初始快甚至优于构造器先执行。有什么差异吗？初始快执行时会首先执行一段**固定的代码**，基于这个原因，初始化块对同一个类中所有的对象所进行的初始化处理完全相同。

4.1 普通初始化快

初始化快是Java类里可出现的第四种成员(成员变量，方法，构造器)。初始化快的修饰符只能是static(并成为静态初始化块)

4.2 静态初始化快

又称为类初始化快，负责对类进行初始化。静态初始化块是类相关的，系统将在类初始化阶段执行静态初始化块，而不是在创建对象时才执行。因此要比**普通初始化块先执行**。

### 11. 打印对象和toString方法

Object类提供的toString()总是返回该对象实现类的"类名 + @ + hashcode"值，这个返回值并不能真正的实现自我描述的功能，需要子类去复写。注意所有子类都继承自Object类，所以所有子类都有这个方法。

### 12. == 和 equals方法

==操作符专门用来比较两个变量的**值**是否相等，也就是用于比较变量所对应的内存中所存储的数值是否相同，要比较两个基本类型的数据或两个引用变量是否相等，只能用==操作符。

如果一个变量指向的数据是对象类型的，那么，这时候涉及了两块内存，对象本身占用一块内存（堆
内存），变量也占用一块内存，例如Objet obj = new Object();变量obj是一个内存，new Object()是另一个内存，此时，变量obj 所对应的内存中存储的数值就是对象占用的那块内存的首地址。对于指向对象类型的变量，如果要比较两个变量是否指向同一个对象，即要看这两个变量所对应的内存中的数值是否相等，这时候就需要用==操作符进行比较。

equals 方法是用于比较两个独立对象的内容是否相同，就好比去比较两个人的长相是否相同，它比较
的两个对象是独立的。例如，对于下面的代码：
	
	String a=new String("foo");
	String b=new String("foo");

两条new 语句创建了两个对象，然后用a,b 这两个变量分别指向了其中一个对象，这是两个不同的对
象，它们的首地址是不同的，即a 和b 中存储的数值是不相同的，所以，表达式a==b 将返回false，而这两个对象中的**内容**是相同的，所以，表达式a.equals(b)将返回true。

废话不多说，实例代码：

	public class EqualsTest {
    public static void main(String[] args) {
        String s1 = new String("Java");
        String s2 = new String("Java");
        //返回 false，因为比较的是s1的地址，即栈内存
        System.out.println(s1==s2);
        // 比较两个引用对象所指堆内存中的数值，true
        System.out.println(s1.equals(s2));
        // 看两个引用对象所指的数值在堆内存中位置是不是一样的。
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());
    	}
	}




### 13. 接口和抽象类

接口和抽象类很像，都具有如下定义：

* 接口和抽象类都不能实例化，都位于继承树的顶端，用于被其他类实现和继承

* 接口和抽象类都可以包含抽象方法，实现接口和继承抽象类的普通子类都必须实现这种抽象方法。

抽象类和接口之间的差别也很大：

接口作为系统与外界交互的窗口，体现的是一种规范。对于接口实现者而言，接口规定实现者必须对外提供哪些服务(以方法的形式)；对于接口调用者而言，接口规定了调用者可以调用哪些服务，以及如何调用这些服务。

从某种程度上来说，接口类似于系统的总纲。系统的接口不应该常变，一旦变化，对整个系统的影响都是辐射式的。

抽象类则不一样，抽象类作为系统中多个子类的共同父类，体现的是一种模板设计。是一种中间产品，还未成型。

### 14. 匿名内部类

适合创建只使用一次的类，创建匿名内部类时，**会立即创建一个该类的实例，这个类定义立即消失**，匿名内部类不能重复使用。定义匿名内部类格式如下：

	new 实现接口（）| 父类构造器（实参列表）
	{
		//匿名内部类的类体部分
	}
最常用的创建匿名内部类的方式是创建某个接口类型对象，实例程序如下：

	interface Product{
		public double getPrice();
		public String getName();
	}

	public class AnonymousTest{
		public void test(Product p){
		System.out.println("购买了一个" + p.getName + ",花掉了" + p.getPrice());
  
    }

    public static void main(String args[]){
		AnonymousTest ta = new AnonymousTest();
		ta.test(new product({
          	public double getPrice(){
			
				return 567.8;
		
	    }
		public String getName(){
				return "AGP显卡“；
 	   }
     });
		
	}
	}

程序中 AnymousTest 定义了一个Test() 方法，该方法需要传入一个 Product对象作为参数，但是Product只是一个接口，无法直接创建对象，因此此处考虑创建一个Product接口实现类的对象传入该方法-如果这个接口实现类需要复用，需要将其定义成独立类，如果这个接口实现类只需要用一次，则可以定义成匿名内部类的方式。

定义匿名内部类无需 class 关键字，而是在定义匿名内部类时候，直接生成该匿名内部类的对象。并且匿名内部类不能是抽象类。

所以上面创建Product实现类对应的代码，可以写成如下形式：

	class AnonymousProduct implements Product{
		
       	public double getPrice(){
			
				return 567.8;
		
	    }
		public String getName(){
				return "AGP显卡“；
 	   }
    }



### 16.  Java8新增的Lambda表达式

Lambda表达式的主要作用就是代替匿名内部类的繁琐语法，其主要组成部分如下所示：

* 形参列表。形参列表允许省略**形参类型**。如果形参列表中只有一个参数，甚至连形参列表的括号也可以省略

* 箭头（->）

* 代码块。如果代码块只包含一条语句，lambda允许省略代码块的花括号。只有一条 return 语句，甚至可以省略。

Lambda有如下两个限制

* 表达式的目标类型必须是明确的函数式接口
* 表达式只能为函数式接口创建对象。Lambda只能实现一个方法，因此它只能为只有一个抽象方法的接口(函数式接口)创建对象。

### 17. 各种变量之间的区别

[简书答案](http://www.jianshu.com/p/179a094e4114)

### 18.
 [从Java类到对象的创建过程都做了些啥？内存中的对象是啥样的](http://www.jianshu.com/p/ebaa1a03c594)

### 19.
[类和对象运行时在内存里是怎么样的？各种变量、方法在运行时是怎么交互的？](http://www.jianshu.com/p/1b2ded9db25d)


### 20.
[泛型的意义和作用是啥？](http://www.jianshu.com/p/5179ede4c4cf)


### 21.
[Java垃圾回收机制](http://www.jianshu.com/p/778dd3848196)


### 22. "hello" 和 "new String("hello")"有什么区别？

**为什么这种存在两种写法**

当Java直接使用 "hello" 字符串直接量，JVM会使用常量池来管理这些字符串；当使用 new String("hello") JVM会首先用常量池来管理"hello"，再调用String的构造器来创建一个新的String对象，新创建的String对象被保存在堆内存中，也就是 new String("hello"),创建了两个字符串对象。 其创建的对象是运行时创建出来的，保存在运行时内存区，不会保存在常量池。

### 23. **java8增强的包装类**

基本的包装对象有哪些？

1. byte --> Byte
2. short --> Short
3. int --> Integer
4. long --> Long
5. char --> Character
6. float --> Float
7. double -->Double
8. boolean --> Boolean

java 是面向对象语言，但是也包含了8种基本数据类型，这8种基本数据类型不支持面向对象的编程机制，即没有成员变量，方法可以被调用。这些带来的好处显而易见，但是也有弊端。

为了解决8中基本数据类型不能被当成 Object 类型变量使用的问题，java 提供了**包装类**（wrapper class），为 8中基本数据类型分别定义了相应的引用类型。

Java 在基本数据类型和包装类对象之间的转换有点繁琐，之后的 JDK1.5 提供了自动装箱和自动拆箱功能解决这个问题。

看一段代码：

	public class AutoBoxingUnboxing{
		public static void main(String args){

			//直接把一个基本类型变量赋值给Integer对象
			Integer inObj = 5;
			//直接把一个boolean类型变量赋值给一个object类型变量
			Object obj = true;
			//直接把一个Integer对象赋值给int类型变量
			int it = inObj;
			if(boolObj instanceof Boolean){
				//先把Object对象强制类型转换为Boolean类型，再赋给boolean变量
				boolean b = (Boolean)boolObj;
				System.out.println(b);
			}

		}
	}


除此之外，包装类还可以实现基本类型变量和字符串之间的转换，把字符串类型的值转换为基本类型的值有两种方式：

* 利用包装类提供的 parseXxx(String s)(例如 parseInt()) 静态方法，除了 Character 之外，所有的包装类都提供了该方法
* 利用包装类的 Xxx(String s) 构造器


### 24. 数组

foreach 循环：使用其遍历数组和集合时，无需获得数组和集合长度，无需根据索引来访问数组和集合元素，foreach循环自动遍历数组和集合中的每个元素。

 foreach 循环支持的格式如下：

	for(type variableName : array | collection){
			//自动访问每个元素
	}

实例如下：

	String[] books = {"book1","book2","book3"};
	for(String book :books){
		System.out.println(book);
	} 

注意不要对循环变量进行赋值即 book，没有意义。


### 25. 形参个数可变的方法

java允许定义形参个数可变的参数，即如果在定义方法时，在最后一个形参的类型后增加三个点(...),则表明该形参可以接受多个参数值，多个参数值被当成数组传入。实例如下：

	public class Varagrs {

    public static void test(int a,String...books){

        for (String tmp : books)
            System.out.println(tmp);

    }
    public static void main(String args[]){
        test(3,"book1","book2","book3");
    	}
	}

其本质就是采用数组形参来定义方法即：

	public static void test(int a,String[] books);

调用有区别：
	
	test(2,new String[]{"book1","book2"});

### 26. 重写 equals() 和 hashCode()
	
下面这段代码判断了两个对象是否相等：

	
	class Persons{

    private String name;
    private String idStr;

    public Persons(String name, String idStr) {
        this.name = name;
        this.idStr = idStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

     //自己来重写 equals()

    public boolean equals(Object object){

        //如果两个对象为同一对象
        if (this == object) {
            System.out.println("1");
            return true;
        }
        //只有当 obj 是Person 的对象,利用了反射
        if (object != null && object.getClass() == Persons.class){
            // 能理解为什么要上转型吧
            Persons ps = (Persons)object;
            //当前对象的 idStr 与 obj 对象的 idstr 相等 才可以判断两个对象相等
            if (this.getIdStr().equals(ps.getIdStr())) {
                System.out.println("2");
                return true;
            }
        }
        return false;
     }
	}

	public class OverrideEqualsRight {

    public static void main(String[] args) {
        Persons p1 = new Persons("A","123");
        Persons p2 = new Persons("A1","123");
        Persons p3 = new Persons("A1","12");
        System.out.println("p1和p2是否相等"+p1.equals(p2));
        System.out.println("p2和p3是否相等"+p2.equals(p3));
    }
 	}

上面这段代码有好多地方可以仔细理解一下，比如判断 一个对象是否是 类的实例，为什么不用 instanceof 方法？第一我们重写 equals 时，通常要求两个对象是一个类的实例，而 instanceof 则可以判断该对象是**一个类或者该类的子类的**实例，都可以。

Objects 默认提供的 equals 只是比较**对象的地址**，即 Object 类提供的 equals 方法与 == 效果完全相同，因此实际开发中经常需要重写 equals。

另外 **String 已经重写了 Object 的 equals 方法**，只要两个字符串所包含的字符序列相同，则返回 true。表明两个字符串相等。

== 基本类型的话只要数值相等，则相等，引用类型变量，只有它们指向同一个对象，才会返回 true。


参加 8.3.1 重写 hashCode()

在 HashSet 中，存放一个元素时，会调用该对象的 hashCode() 方法来获得该对象的 hashCode()值，然后根据该值决定其在 HashSet 中的存储位置，在 HashSet 中判断两个元素相等的标准是 两个对对象的 equals() 和 hashCode() 都相等。

如果两个对象通过 equals 返回 true，但是 hashcode 方法却返回不同的 hashcode 值，将导致 HashSet 会把这两个对象保存在 Hash 表中不同位置，从而两个对象都可以添加成功，这就与 Set 集合的规则冲突了。

如果 equals 返回 false，但是 hashcode 返回 true ，则比较麻烦，因为两个对象 hashcode 相同，程序试图把它们放在同一个位置，但又不行，会在这个位置采用链式结构来保存多个对象，hashset 访问元素也是根据 hashcode数值来决定的，所以这样会导致访问效率下降。


### 27. Java中Comparable和Comparator区别（简单实例）

1、相同点：都是用来实现集合中元素大小的比较。

2、不同点： 

   * Comparable采用的是内部比较法（本身有默认的比较规则），而Comparator属于外部比较法（一个外部的比较器，开发人员可以实现定义的比较规则）。
    
   * 采用Comparable比较时，集合中的元素必须实现Comparable接口（比如String和Integer），使用的比较方法是：int compareTo(T o); 
   
   * 采用Comparator比较时，使用的比较方法是：int compare(T o1 ,T o2); 
   
   * Comparable位于java.lang包中（无需导入），而Comparator位于java.util包中（需要导入）