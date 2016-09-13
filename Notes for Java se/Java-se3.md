---
title: 深入理解 Java 中的泛型
date: 2015-12-13 09:21:55
tags: java
categories: About Java
---

读书笔记之 Java 泛型

**泛型不仅仅可以增强程序的健壮性也可以增加代码的复用**

<!-- more -->

### 一 . 泛型引入

集合的缺点就是把元素扔进 集合之后，集合就会忘记这个对象的数据类型，当再次取出该对象时，该对象的编译类型就变成了 Object 类型(运行时类型没有变)，而这样的话，我们再次从集合中取出，就需要进行强制类型转换。带来的问题是不仅使代码臃肿而且容易带来 ClassCastException 异常。

Java 泛型的设计原则就是，只要代码在**编译时**没有出现警告，就不会在运行时出现 ClassCastException异常。

### 二 . 泛型语法

``` java
List<String> list = new ArrayList<String>();
Map<String,Integer> map = new HashMap<String,Integer>();
```

其实完全可以写成这样：

``` java
List<String> list = new ArrayList<>();
Map<String,Integer> map = new HashMap<>();
```

一个实例程序如下：

``` java
public class DiamondTest {

    public static void main(String[] args) {

        List<String> books = new ArrayList<>();
        books.add("java");
        books.add("python");
        books.add("javascript");
        // books.forEach(ele -> System.out.println(ele));
        books.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
        // 泛型中套泛型
        Map<String,List<String>> schoolsinfo = new HashMap<>();
        List<String> schools = new ArrayList<>();
        schools.add("schools1");
        schools.add("schools2");
        schoolsinfo.put("孙",schools);
        schoolsinfo.forEach((key,value) -> System.out.println( key + "---" + value));
    	}
}	
```

### 三. 深入泛型

* 定义泛型接口,类

看集合中 List 接口定义时就是泛型的，如下：

``` java
public interface List<E> extends Collection<E> {

       Iterator<E> iterator();
   	   E get(int index);
}
```

由此我们不用看 ArrayList 源码，就知道肯定有下面一句：

``` java
class ArrayList<E> implements List<E>
// 而我们程序员肯定在编译器中这样去引用：
List<String> list = new ArrayList<String>();
// 可以如下简写
List<String> list = new ArrayList<>();
```

这个流程简直完美。


* 从泛型类派生子类

可以为一个类，增加泛型声明，如下：

``` java
public class AppleClassTest<T> {
    private T info;
    public AppleClassTest(){}
  	// 注意这里的构造器，并没有变化
    public AppleClassTest(T info){
        this.info = info;
    }

    public void setInfo(T info){
        this.info = info;
    }

    public T getInfo(){
        return this.info;
    }

public static void main(String args[]){

        AppleClassTest<String> a1 = new AppleClassTest<>("苹果");
        System.out.println(a1.getInfo());

        AppleClassTest<Double> a2 = new AppleClassTest<>(3.43);
        System.out.println(a2.getInfo());
	}
}
```


也可以继承父类，复写父类的方法，但是用到泛型参数的时候，是与父类一致的。

*  并不存在泛型类

有一种错觉，ArrayList<String> 是 ArrayList<Object> 的子类，确实很像，但是系统并没有为 ArrayList<String> 生成新的 class 文件，也不会将 ArrayList<String> 当成新类来处理。不要自认为 String 是 Object 的子类，那么前面的情况就会成立。

crzay java 上面有一个这样的程序：

``` java
List<String> l1 = new ArrayList<>();
List<Integer> l2 = new ArrayList<>();
sout(l1.getClass() == l2.getClass());//true
```

以上程序在运行时，总是有相同的 class。另外 不管为**泛型的类型形参传入哪一种类型实参**，对于 java 来说，它依然被当成同一个类来处理，在内存中也只占用一块内存空间，因此在静态方法，静态初始化快或者静态变量的声明中不允许使用类型形参。

如下代码：

``` java
class R<T>{

    //static T info;
    T age;
    public void bar(T msg){
        System.out.println(msg);
    }
    //public static void bar(T msg){}
    public R(T age){
        System.out.println(age);
    	}
}

public class GenericClassTest {

    	public static void main(String[] args) {

        R<String> r1 = new R<>("java");
        R<Integer> r2 = new R<>(12);
        System.out.println(r1.getClass()==r2.getClass());
        r1.bar("r1");
        r2.bar(17);
    	}
}
```

### 四 . 类型通配符

需要定义一个方法，方法里有一个集合形参，集合形参里面的元素又是不确定的。开始自然想到的是使用 Object [即上面的做法]来代替形参类型，但是所带来的问题很多。

因此我们会采用类型通配符来替代：

``` java
public void test(List<?> c){
		for(int i=0;i<c.size;i++)
			Sys.out(c.get(i));
}
```

实例：

``` java
public class FxTest {

    public static void test(List<?> c){

        for (int i=0;i< c.size();i++ ){

            System.out.println(c.get(i));
        }
    }
    
public static void main(String args[]){

        ArrayList<String> book = new ArrayList<>();
        book.add("b");
        book.add("a");
        test(book);

        List<Integer> score = new ArrayList<>();
        score.add(90);
        score.add(80);
        test(score);
    }
}
```

上述这种带通配符(?)的 list 仅表示它是各种泛型 List 的父类，并不能将元素加入其中，如下实例就会引起错误：

``` java
List<?> c = new ArrayList<String> ();
c.add(new Object);// 会引起编译错误
```

因为程序无法确定 **c 集合中**数据的类型，所以不能向其中添加对象。





### 五 . 设定类型通配符上限

当直接使用 List<?> 这种形式时，即表明这个 List 集合可以是任何**泛型 List 的父类**。但是我们并不像其为所有泛型 List 的父类。

从而有语法来限制 List < ? extend XXX>  此处 ？ 是未知类型，但是它一定要是 XXX 的子类型或者其本身。由此也可以知道，不能随便向集合中添加元素，因为**集合类型是未知的**。

``` java
public class WildCardTest {

    public static void test(List<? extends Number> c){

        for (int i = 0; i < c.size(); i++) {
            System.out.println(c.get(i));
        }
}

	public static void main(String[] args) {

        List<String> l1 = new ArrayList<>();
        l1.add("java");
        l1.add("c++");
        l1.add("python");
        //test(l1);

        List<Integer> l2 = new ArrayList<>();
        l2.add(1);
        l2.add(2);
        l2.add(3);
        test(l2);

        List<Double> l3 = new ArrayList<>();
        l3.add(12.22);
        l3.add(13.4);
        test(l3);
   		 }
}
```
从上而知，Number 子类 有 Integer，Double...
我们指定 ？ extend Number 所以 String 无法添加进去。


### 六 . 设定类型形参的上限

Java 泛型不仅仅允许在使用通配符形参时设定上限，而且可以在定义类型形参时设定上限，用于表示传给该类型形参的实际类型要么是该上限类型，要么是该上限类型的子类。如下实例：

``` java
public class Apples<T extends Number> {
    
    T col;
    public static void main(String args[]){
        
        Apples<Integer> ai = new Apples<>();
        Apples<Double> ad = new Apples<>();
        // String 不是 Number 的子类，故编译会出错
        Apples<String> as = new Apples<>()
                
    	}
}
```

### 七 . 泛型方法


定义类,接口时没有使用类型形参，但是定义方法时想自己定义类型形参。

定义泛型方法：

下面代码负责将一个 Object 数组的所有元素添加到一个 Collection 集合中：

	static void fromArrayToCollection(Object[] a,Collection<Object> c ){
			for(Object o:a){
				c.add(o)
			}
	}

本来方法应该没有问题的，但是前面知道了 Collection<String> 并不是 Collection<Object> 的子类，它只能将 Object[] 数组里面的元素赋值到 元素为 Object 的集合中。

为此 Java5 提供了泛型方法，可改写成如下形式：

	static <T> void fromArrayToCollection(T[] a,Collection<T> c ){
			for(T o:a){
				c.add(o)
			}
	}

与类，接口中使用泛型参数不同，方法中的泛型参数无需显式传入实际类型参数，即调用fromArrayToCollection(),无需在调用该方法前传入 String，Object等，编译器可以根据实参推断出形参类型。

为了能让编译器准确推断出泛型方法中类型形参的类型，不要乱搞：

``` java

public class ErrorTest {
    
    static <T> void test(Collection<T> from,Collection<T> to){
        
        for(T ele : from){
            to.add(ele);
        }
    }
    
    public static void main(String args[]){

        List<Object> as = new ArrayList<>();
        List<String> ao = new ArrayList<>();
        //引起编译错误，要求 T 相同
        test(as,ao);
        
   		 }
}
```

为了避免这种错误，可以将 Collection<T> from 改成 Collection<? extend T>即可，道理很简单。



### 八 . 泛型构造器

很简单，就是格式要经常用才能熟悉，代码如下：

``` java
class Foo{

    public <T> Foo(T t){
        System.out.println(t);
    }

    public Foo(String s){
        System.out.println("指定了类型的 construct");
        System.out.println(s);
    	}
 	}

	public class GenericConstructorTest {

    public static void main(String[] args) {
        //泛型构造器中 T 为 String
        new Foo("疯狂java讲义");
        //泛型构造器中 T 为 Integer
        new Foo(200);
        //显示指定了类型
        new <String> Foo("crazy java");
        //new <String> Foo(12.3);
    	}
}
```

### 九. 设定通配符下限

通配符下限的引出非常麻烦，场景：将 src 集合中的元素赋值到 dest 中。我们采用通配符下限的话就需要遵循：不管 src 集合的类型是什么，只要 dest 集合元素类型与其相同或者是其父类即可。为了表达这种约束关系， java 允许使用通配符下限：<? super Type> 这个通配符表示 它必须是 Type 本身或者是 Type 的父类。实例代码如下：

``` java
public class MyUtils {
    //dest 集合元素必须与src集合中的元素类型相同或者是其父类
    //容易理解把src中元素复制到 dest 中
    public static <T> T copy(Collection<? super T> dest,
                             Collection<T> src)
    {
        T last = null;
        for(T ele : src){
            last = ele;
            dest.add(ele);
        }
        return last;
    }

    public static void main(String[] args) {
        List<Number> in = new ArrayList<>();
        List<Integer> li = new ArrayList<>();
        li.add(5);
        li.add(4);
		//此处可以准确的直到最后一个元素类型
        Integer last = copy(in,li);
        System.out.println(last);
     }
}
```

### 十 . 擦除和转换

当把一个具有泛型信息的变量赋值给一个没有泛型信息的变量时，所有尖括号之间的信息都将被扔掉。实例程序示范了这种擦除：

``` java
class Apple<T extends Number>{
    public T getSize() {
        return size;
    }

    public void setSize(T size) {
        this.size = size;
    }

    T size;
    public Apple(){}
    public Apple(T size){
        this.size = size;
    	}
	}
	public class ErasureTest {

   	 public static void main(String args[]){
        Apple<Integer> a = new Apple<>(6);
        // a的getSize()方法返回 Integer对象
        Integer as = a.getSize();
        // 把a对象赋值给b，丢失尖括号里面的类型信息
        Apple b = a;
        // b只知道size的类型是number
        Number size1 = b.getSize();
        //下面的代码出现变异错误
        //  Integer size2 = b.getSize();
    	}
}
```

上面程序中定义了一个带泛型声明的 Apple 类，其类型形参的上限是 number ，这个类型形参用来定义 Apple 类的 size变量。注意一点，因为 Apple 的类型形参上限是 Number，所以编译器依然知道 b 的getSize() 方法返回 Number类型，但是具体是 Number的哪个子类就不知道了。

另外，从上面看来我们很容易觉得 直接把一个 List 对象赋给 List<String> 对象应该引起编译错误，但实际不会。仅仅会提示 "未经检查的转换错误"

``` java
public class ErasureTest2 {

    	public static void main(String args[]){

    	    List<Integer> list = new ArrayList<>();
    	    list.add(3);
    	    list.add(2);
	
    	    List li = list;
    	    // 引起"未经检查的警告"，但编译，运行正常。
    	    List<String> ls = li;
    	    // 运行会报错
    	    System.out.println(ls.get(0));

    	}
}
```

第二句报错提示：

	Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String


### 总结

泛型的作用就是增强程序的健壮性，在编译时检查元素的类型是否符合规范。