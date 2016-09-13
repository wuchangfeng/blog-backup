---
title: Java 注解浅析
date: 2015-12-15 11:49:06
tags: java
categories: About Java
---

读书笔记之 java注解

<!-- more -->

## Annotation 的概念和作用

这些东西其实是代码里的特殊标记，但不是注释，不是注释。这些标记可以在编译时，类加载时以及运行时被读取，并进行相应的处理。通过使用注解，开人员可以在不改变原有逻辑的情况下，补充一些信息。


## 基本的 Annotation

* @override 注释的功能和用法

* @Deprecated 注释的功能和用法

* @SuppressWarning 抑制编译器警告

* java7的 "堆污染"警告 与 SafeVaragrs

* java8的函数式接口 与 @Functionallnterface

函数式接口，即某个接口中只有一个抽象方法，注意可以有别的普通的方法。而@Functionallnterface 则是用来指定某个接口必须是函数式接口。用来保证该接口中，只有一个抽象方法，如果不是，则会报错。


## JDK 的元 Annotation

JDK 元 Annotation 用于修饰 其他 Annotation 的定义

* @Retention

@Retention只能用于修饰 Annotation 定义，用于指定被修饰的 Annotation 可以保留多长时间。

其包含一个 RetentionPolicy 类型的 value 成员变量，所以使用 @Retention 时必须为该成员变量赋值。

RetentionPolicy.Class :编译器把 Annotation 记录在 class 文件中。当 运行 java 程序时，JVM 不可获取 Annotation 信息，其为默认值。当 java 程序运行时，JVM 将其抛弃。

RetentionPolicy.RUNTIME:...(同上)...Jvm 也可以通过反射获取注解信息，JVM执行时也不会丢弃它，程序可以通过反射提取注解信息。

RetetionPolicy.SOURCE:Annotation 只保留在源代码中，程序**编译**时直接丢弃这种 Annotation。


* @Target

同样这个也只能修饰 Annotation 定义，它用于指定被修饰的 Annotation 能用于修饰哪些程序单元。即方法啊，构造函数啊还是修饰成员变量。

* ElementType.ANNOTATION_TYPE : 指定该Annotation只能修饰Annotation。
* ElementType.CONSTRUCTOR: 指定只能修饰构造器。
* ElementType.FIELD: 指定只能成员变量。
* ElementType.LOCAL_VARIABLE: 指定只能修饰局部变量。
* ElementType.METHOD: 指定只能修饰方法。
* ElementType.PACKAGE: 指定只能修饰包定义。
* ElementType.PARAMETER: 指定只能修饰参数。
* ElementType.TYPE: 指定可以**修饰类，接口，枚举定义**。



实例：

```java
@Target(ElementType.FIELD)
public @interface ActionListtener{ }
```

* @Documented

修饰的Annotation类可以被javadoc工具提取成文档

* @Inherited

@Inherited 元 Annotation 指定被它修饰的 Annotation 将具有继承性-如果某个类使用了@Xxx注解(定义该类时使用了 @Inherited修饰)，则其子类自动被@Xxx修饰。

## 自定义 Annotation

* 定义 Annotation

简单点的：

```java
public @interface Test{
  
}
```



复杂点的：

```java
public @interface Test{
	String name();
	int age(); 
}
```

可以在定义注解时设置默认值，也可以在引用时设置。

## 提取 Annotation信息

使用 Annotation 修饰了类，方法，成员变量之后，他们不会自己生效，需要开发者利用工具来提取信息。

java.lang.reflect 提供了一些实现反射功能的工具，提供了读取运行时 Annotation 的能力。即定义 Annotation 时，使用了 @Retention(RetentionPolicy.RUNTIME) 修饰，该 Annotation 才会在运行时可见，JVM 才会装载 *.class 文件时，读取保存在 class 文件的 Annotation。

主要是 AnnotatedElement接口，该接口主要有一下几个实现类：

* Class：类定义
* Constructor：构造器定义
* Filed：类的成员变量定义
* Method:类的方法定义
* Package：类的包定义

故而程序通过反射获取了某个类的 AnnotatedElement 对象之后(如class，Method，constrcutor)
程序就可以调用对象的方法获取 Annotation信息。

简单实例，打印出所有注解：


```java
Annotation[] array = Class.forName("Test").getMethod("info").getAnnotions();
for(Annotation an : array)
	sys.out(an);
```

实例写上，很多地方不写实例根本不会注意到：

```java
@Target(ElementType.METHOD)//修饰方法
@Retention(RetentionPolicy.RUNTIME)//指定时机
@Inherited//具有继承性
@interface Inheritable{
	//成员变量以方法的形式声明
	String name();
	int age();
 }
 
class Base{
@Inheritable(name="Java",age = 13)
public void info(){
    // 下面这句编译不会通过，不能直接使用这些注释的值，需要通过反射获取
    //System.out.println(name);
 }
}

public class AnnotationTest extends Base{

public static void main(String[] args) throws Exception{
    //获取 base 类的 info 方法的所有注解
    Annotation[] arrsy = Class.forName("Base").getMethod("info").getAnnotations();
    for (Annotation an : arrsy)
        System.out.println(an);
    //打印 Annotation 是否有 Inheritable 修饰
    System.out.println(AnnotationTest.class.isAnnotationPresent(Inheritable.class));
}
```



### 编译时提取注解信息

Butterknife 最新的实现方式主要是编译时注解，以前学习注解，主要学习的还是运行时，然后根据反射来获取相关信息，但是反射一旦使用较多，会导致一些性能的下降。

而编译时是注解，因而没有使用反射，因此相当于直接调用。


``` java
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface InjectView
{
	int value();
}
```

其中的 RetentionPolicy.CLASS 就说明注解是编译时动态处理的