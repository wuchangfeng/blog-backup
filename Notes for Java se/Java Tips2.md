---
title: Java 常用知识点总结2
date: 2015-09-01 13:47:30
tags: java
---

java常用知识点总结2

<!-- more -->

### 0.字节与字符之间的区别

**字节**是存储容量的基本单位，1字节=8个二进制位。**字符**是指字母、数字、汉字和各种符号。一个字符在计算机中用若干个字节的二进制数表示，西文字符1个字节，中文字符2个字节。

计算机存储信息的最小单位，称之为**位（bit）**，音译比特，二进制的一个“0”或一个“1”叫一位。

### 1. 如何重写 hashCode() 

参考 疯狂java 8.3.1

### 2. static 关键字作用

其实它只是为了区分所修饰的东西是属于实例还是属于类。

### 3. java 构造器不返回值，为什么不用 void 修饰。

当用 new 关键字来调用构造器时，会返回该类的实例，只不过他的返回时隐式的。

### 4. this 关键字

* 在构造器中引用该构造器正在初始化的对象
* 在方法中引用调用该方法的对象

另外 static 修饰的方法中，不能有 this 关键字，很简单吧？能理解吗？


### 5. ConcurrencyHashMap是怎么实现的？什么机制？

很重要

### 6. synchronized与volatile之间有什么关系？以及transient关键字是干嘛的？

[synchronized与volatile](http://www.tuicool.com/articles/B3Q3IfM)

这个东西要和java的序列化一起来说，在java对象序列化的时候如果某些对象里面的字段不需要序列化，就定义为transient ? 对吗？还待验证。



### 7. foreach 和 for 循环之间的效率比对，arraylist以及map的实现？

Effective java 中有

### 8. 单例类Singleton

大部分时候把类的构造器定义成 public 访问权限，允许任何类自由创建该类对象，这样会造成系统的开销很大。如果一个类，始终只能创建一个对象，即称为 Singleton。设计该类的时候：首先用 private 封装该构造器，根据封装原则，一旦把该类的构造器影藏起来，就要提供一个 public
方法作为该类的访问点，用于创建该类对象。并且该方法只能用 static 修饰，这句话很有意思，仔细想想。另外，必须具有缓存已经创建过的对象。实例代码如下：

	class singleton{
    //使用类变量来缓存曾经创建的实例
    private static singleton  instance;
    private singleton() {
    }
    //提供一个静态方法，来保证只有一个 singleton实例
    public static singleton getInstance() {

        if (instance == null){
            instance = new singleton();
        }
        return instance;
    	}
	}

	public class SingletonTest {
    
    	public static void main(String[] args) {

        singleton s1 = singleton.getInstance();
        singleton s2 = singleton.getInstance();
        System.out.println(s1==s2);
        //singleton s2 = new singleton();
    	}
	}

### 9. final 修饰符

final 修饰的变量为不可被改变，一旦获得了初始值，该值就不能被重新赋值。


#### final 成员变量

#### final 局部变量

#### final 修饰的基本类型变量和引用类型变量的区别


#### 可执行 "宏替换"的 final 变量

对于一个 final 变量来说，满足如下三个条件，即可认为该 final 变量为一个**直接量**了：

1. 使用 final 修饰符修饰
2. 定义该 final 变量时设置了初始值
3. 该初始值在**编译时**就能被确定下来

另外，java 会使用常量池来管理曾经用过的字符串直接量，如执行 String a = "java",常量池中就会缓存一个字符串 "java" ,因此如果程序再次执行 String b = "java",系统会直接让 b 指向常量池中的"java"字符串，因此 a == b,会输出 true。

如下实例：s2 引用的字符串常量可以在编译时就被确定，因此s2直接引用常量池中已经有的"疯狂java"
字符串，这个就是在编译阶段就能确定s2的值。所以系统会让 s2 直接指向常量池中缓存的"疯狂java"字符串。因此 s1 == s2 将输出true。

对于 s3 而言，它的值是由 str1 和 str2进行连接运算后得到的，由于只是普通变量，编译器不会执行宏替换，因此编译器无法再编译时确定 s3 的值，也无法让 s3 指向 字符串池缓存的"疯狂java"，因此 s1 == s3 将输出 false。

	public class StringJoinTest {

    public static void main(String[] args) {
        //输出true
        String s1 = "疯狂java";
        String s2 = "疯狂" + "java";
        System.out.println(s1==s2);
        //输出false 为什么？
        String str1 = "疯狂";
        String str2 = "java";
        String s3 = str1 + str2;
        System.out.println(s1 == s3);
        //输出true  为什么？执行宏替换，让其编译时就确定
        final String str3 = "疯狂";
        final String str4 = "java";
        String s5 = str3 + str4;
        System.out.println(s1 == s5);
    	}
	}

### 10. final 方法

很简单，不想被子类重写。 java 提供的 Object类就有一个 final 方法：getClass().

### 11. final 类

该类不可以被重写，保证一些细节的安全。java.lang.Math 就是这样一个类。


### 12. 不可变类

不可变类(immutable)是指创建该类的实例后，该实例的实例变量是不可改变的。java 提供的8个包装类和 java.lang.String 类都是不可变类。




### 13. Object 类

java 中所有类都是 object 类子类。常用方法如下


1. boolean equals(Object obj):判断对象与对象是否相等。
2. protected void finalized(): 当系统中没有引用变量引用到该对象时，垃圾回收机制调用该方法
回收，清理该对象资源。
3. Class<?> getClass():返回对象在运行时类。
4. int hashcode():返回对象的hashcode值。默认情况下，都是根据对象的地址来计算，但是很多类都会重写。
5. String toString():返回对象的字符串表示
6. object 还提供了 wait(),notify(),notifyAll()这几个方法，可以控制线程的暂停和运行，注意**这些不是 Thread 提供的**。

另外 Object 类还提供了 clone() ,说其克隆的方法比copy快速了2倍。但是它只是一种浅克隆，只克隆该对象所有成员变量的数值，不会对引用类型的成员变量所引用的对象进行克隆。


### 14. String StringBuffer StringBuilder类

字符串就是一连串的字符序列，java 提供了 String和StringBuffer两个类来封装字符串。

String类是不可变类，即一个string对象被创建之后，包含在这个对象中的字符序列是不可以被更改的，直到该对象被销毁。

StringBuffer类是可变的，被创建之后，可以利用StringBuffer提供的append(),insert(),reverse(),setCharset(),setLength()等方法改变这个字符串对象的字符序列，一旦生成了最后想要的，就可以调用它的toString()方法转换成String对象。

之后提供的 StringBuilder则跟StringBuffer差不多的，只是StringBuffer是线程安全的，StringBuilder则不是，所以StringBuilder效率较高。

StringBuilder 一个实例如下：

	public class StringBuilderTest {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        sb.append("java");
        sb.insert(0,"hello ");
        System.out.println(sb);

        sb.replace(5,6,",");
        System.out.println(sb);

        sb.delete(5,6);
        System.out.println(sb);

        sb.reverse();
        System.out.println(sb);

        System.out.println(sb.length());
        System.out.println(sb.capacity());

        sb.setLength(5);
        System.out.println(sb);
    	}
	}

输出如下：

	hello java
	hello,java
	hellojava
	avajolleh
	9
	16
	avajo

### 15. switch()能否用 string 做参数？
答：java 7 之后可以。


### 16. 异常处理

java 将异常分为两种，checked异常和runtime异常。

* 使用 finally回收资源。

有些时候，java 在 try 里面打开了一些物理资源，如数据库连接，网络连接，磁盘文件等等，由于垃圾回收机制不会回收任何物理资源，所以需要人为手动的关闭这些。肯定是在 finnally 中回收啊。一个简单实例如下：

	public class FinallyTest {

    public static void main(String[] args) {

        FileInputStream fis = null;

        try {
            fis = new FileInputStream("a.txt");
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            //使用 return 强制返回
            return;
            //若执行这句，则finally都不会执行了。
            //System.exit(1);
        } finally {

            if ( fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("执行资源finaly回收");
        	}
    	}
	}

结果：

	a.txt (系统找不到指定的文件。)
	执行资源finaly回收

由上面可以看到，即使在try里面执行了 return，但是 finally 里面的代码，还是会执行的。


### 17. java8 增强的工具类 Arrays

Arrays 类提供的一些静态方法可以直接来操作数组

* int binarySearch(type[] a,type key):使用二分法查询 key 在 a 数组中的出现的索引
* type[] copyOf(type[] original,int length): 把 origin数组复制成一个新的数组，其中 length是新数组的长度
* boolean equals(type[],type[] a2):如果a数组和a2数组长度相等，并且元素也一一相同，返回true
* void fill(type[] a, type var):把 a 数组里面的所有元素都赋值为 var
* sort(type[] a):排序
* String toSTring():数组转换成字符串

当然 java 8 更是新增加了一些方法，并发支持。


### 18. java 位运算

* &：按位与，当两个同时为一时才返回一
* |：按位或，只要有一个位置位一就返回一
* ~：按位非，将操作数的每个位置取反，包括符号位
* ∧：按位亦或，两位相同时返回0，不同时返回1
* <<：
* >>：
* >>>：无符号右移运算符

``` java

	public class BitOperatorTest {

    public static void main(String[] args) {

        int a = 4;
        int b = 8;
        //分别左移一两位
        System.out.println(a<<1);//8
        System.out.println(a<<2);//16
        //分别右移一两位
        System.out.println(a>>1);//2
        System.out.println(a>>2);//1
        //无符号左移一两位
        System.out.println(a>>>1);//2
        System.out.println(a>>>2);//1
        System.out.println(a>>>3);//0

        System.out.println(b>>>1);//4
        System.out.println(b>>>2);//2
        System.out.println(b>>>3);//1
        System.out.println(b>>>4);//0
        System.out.println(b>>>5);//0
        System.out.println(b>>>6);//0

        //按位与,或
        System.out.println(5 & 9);//1
        System.out.println(5 | 9);//13
    }
}

```

5 & 9 以及 5 | 9原理如下：

 00000101      00000101
 
 00001001      00001001
 
&00000001     |00001101

~ -5 = 4 

负数在计算机中以补码的形式存在，所以我们要计算到补码

-5 的原码即二进制如下，第一位为符号位

10000000000000000000000000000000000101

上面第一个1 是符号位，表示其为负数，取反得到反码

11111111111111111111111111111111111010

反码加1得到补码

11111111111111111111111111111111111011

取反后最高位(符号位为0)，成为正数

00000000000000000000000000000000000100

从而其值为 4，由上面的过程，我们可以知道**正数的补码和原码相同**

5 ^ 9 = 12

计算过程如下

00000101

00001001

00001100

则 00001100 值为 12(前面省略了 24个0)，规则就是 上下两位相同得到 0 ，不同得到 1


下面来演示 左移运算的过程 -5 << 2

由于负数在计算机中以补码的形式存在，所以操作的最基本的单位就是补码 -5 补码如下：

   11111111111111111111111111111011

 1111111111111111111111111111101100

如上，左边两个1 被截断出去了，补上两个0,最高位1，表明是一个负数，即-20

Java 右移运算符有两个 >>,>>>,对于 >> 而言，把第一个操作数，二进制码右移指定位置之后，左边空出来的以**符号位**来补充，>>> 是无符号右移，把一个操作数的二进制码右移指定位数后，左边空出来的位，总是以 **0**来补充。


对于正数而言，只要被移动的二进制码没有发生有效位的数字丢失(对于正数而言，所有移除的都是0)，不难发现 左移 n 位就相当于乘以 2 的 n 次方，右边移动则是初一 2 的 n 次方

而扩展后，有新的运算符出现即 <<=;对于 x <<= y,表示的就是 x = x << y



### 19. java 内存泄露与内存溢出


理解这两个概念非常重要。
 
内存泄露：指程序中动态分配内存给一些临时对象，但是对象不会被GC所回收，它始终占用内存。即被分配的对象可达但已无用。
 
内存溢出：指程序运行过程中无法申请到足够的内存而导致的一种错误。内存溢出通常发生于OLD段或Perm段垃圾回收后，仍然无内存空间容纳新的Java对象的情况。
 
从定义上可以看出内存泄露是内存溢出的一种诱因，不是唯一因素。

[具体参考](http://blog.csdn.net/shimiso/article/details/21830871)


### 20. JRE 和 JDK 有什么区别

JRE(运行时环境):核心API，集成API，用户界面API，发布技术，Java虚拟机
JDK(开发环境)：编译java程序的编译器(javac)