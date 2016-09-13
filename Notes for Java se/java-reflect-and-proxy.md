---
title: 反射机制以及动态代理
date: 2015-12-14 15:39:02
tags: java
categories: About Java
---

* 通过反射获取类的信息
* 使用反射生成 JDK 动态代理
* 反射和泛型
  <!--more-->

## 一 . 通过反射查看类信息

* 概念

Java 许多对象都会在运行时出现两种类型：编译时类型和运行时类型。eg：
​	
```java
 Person p = new Student()
```

编译时类型为 Person，运行时类型为 student。**程序运行时需要发现类和对象的真实信息**，解决该问题有两种办法。

1：完全知道类和对象的信息，这种情况可以先使用 instanceof 进行判断，再利用强制类型转换将其转换成运行时类型变量即可。

2：编译时无法完全知道类和对象的所有信息，这是只能在程序运行时来发现其所有信息，这就要用到反射了。


**关于 Class 类**

类是java.lang.Class类的实例对象，而Class是所有类的类（There is a class named Class）
对于普通的对象，我们一般都会这样创建和表示

``` java
Test test1 = new Test();
```

但是查看 Class 类源码发现，其构造器是私有的，显然只有 JVM 能搞定。故我们而不能 new 出实例

``` java
private  Class(ClassLoader loader) { 
    classLoader = loader; 
}
```
故而通过以下方法来获取 Class 对象


1: 使用 Class 类的 forName(String clazzName) 静态方法。其中的字符串内容可能是： java.util.Date，其可能会抛出的异常是 ClassNotFoundException .

2：调用某个类的 class属性来获取该类的 Class对象。 eg：Person.class 将会返回 Person 类对应的 Class 对象。

3：调用某个对象的 getClass() 方法。


* 从 Class 中获取信息

* Class 对应类所包含的构造器
* Class 对应类所包含的方法
* Class 对应类所包含的成员变量
* Class 对应类所包含的 Annotation
* Class 对应类所包含的内部类
* Class 对象对应类所继承的父类
* Class 对象还提供了以下方法来判断该类是否为接口，枚举，注解等类型。


没什么好说的，可以查 API文档，但是注意一点：对于重载的方法，获取其相应信息，需要指定参数，不然返回的可能是多个重载方法中的任意一个！
​    
``` java
	class.getMethod("info",String.class);
	class.getMeathod("info",String.class,Integer.class);
```

废话不说，eg如下：

``` java
//定义可重复注解
	@Repeatable(Annos.class)
	@interface Anno{}
	@Retention( value = RetentionPolicy.RUNTIME)
	@interface Annos{
   		 Anno[] value();
	}
	//使用四个注解修饰该类
	@SuppressWarnings(value="unchecked")
	@Deprecated
	//使用重复注解修饰该类
	@Anno
	@Anno
	public class ClassTest {

    private ClassTest() {
    }
    private ClassTest(String name) {

        System.out.println("执行有参数的构造器");
    }
    public void info(){

        System.out.println("执行无参数的方法");
    }

    public void info(String str){

        System.out.println("执行有参数的方法" + str);
    }

    //定义一个测试用的内部类

    class Inner{

    }
    public static void main(String[] args)
            throws Exception{

        Class<ClassTest> clazz = ClassTest.class;

        Constructor[] constructors = clazz.getDeclaredConstructors();
        System.out.println("ClassTest 的全部构造器如下：");4/3/2016 2:38:42 PM 4/3/2016 2:38:44 PM 
        for (Constructor c : constructors )
            System.out.println(c);

        Method[] mtds = clazz.getMethods();
        System.out.println("ClassTest 的全部方法如下");
        for (Method m : mtds)
            System.out.println(m);

        System.out.println("ClassTest 里带一个字符串参数的info方法为：" + clazz.getMethod("info",String.class));

        //获取 class 对象所对应类的全部注解
        Annotation[] an = clazz.getAnnotations();
        System.out.println("ClassTest 的全部注解如下");
        for (Annotation a : an)
            System.out.println(a);

        System.out.println("该class 上的 @Anno 注解为："+ Arrays.toString(clazz.getAnnotationsByType(Anno.class)));

        //获取 class 对象所对应的全部内部类
        Class<?>[] inners = clazz.getDeclaredClasses();
        System.out.println("Classtest全部内部类如下：");
        for (Class ca : inners)
            System.out.println(ca);

        System.out.println("Classtest的包为：" + clazz.getPackage());
        System.out.println("Classtest的父类为：" + clazz.getSuperclass());
    	}
	}
```


在上面我们看到这样一句：

	Class<ClassTest> clazz = ClassTest.class;

便是通过在这样一个方法来获取 class 对象的。另外，虽然定义 ClassTest 类的时候，使用了@SuppressWarning 注解，但是程序运行时无法分析出该类包含的该注解，因为@SuppressWarning 使用了 @Retention(value = SOURCE)来修饰，表明了@SuppressWarning 只能保留在源码级别上。


### 五 . 使用反射生成JDK动态代理

#### 自定义生成代理对象

程序中可以先生成一个动态代理类，然后通过动态代理类来创建代理对象的方式来生成一个动态代理对象。

``` java
// 创建一个 InvocationHandler 对象
InvocationHandler handler = new MyInvocationHandler(...);
// 使用 Proxy 生成一个动态代理类
class ProClass = Proxy.getProxyClass(Foo.class.getClassloader(),new Class[] {Foo.class});
// 获取 proxyClass 类中带有一个 InvocationHandler 参数的构造器
Constructor ctor = proxyClass.getConstructor(new Class[]{InvocationHandler.class});
// 调用 ctor 的 newInstacnce 方法来动态创建实例
Foo f = (Foo)ctor.newInstance(new Object[]{handler});
```

简化如下：

``` java
// 创建一个 InvocationHandler 对象
Invocationhandler handler = new MyInvocationHandler(...);
// 使用 Proxy 直接生成一个动态代理对象
Foo f = (Foo)Proxy.newProxyInstance(Foo.class.getClassLoader(),new Class[]{Foo.class,handler});
```


## 二 . JDK 动态代理

使用 Proxy 和 InvocationHandler 来生成代理对象，来一个实例来说明动态代理的创建过程：

``` java
interface Persons{

    void walk();
    void sayHello(String name);
}
class MyInvokationHandler implements InvocationHandler{
    /**
     *
     * @param proxy 代表动态代理对象
     * @param method 代表正在执行的方法
     * @param args 代表调用目标方法时传入的实参
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("------正在执行的方法：" + method);

        if (args != null){
            System.out.println("下面是执行该方法传入的实参为：");

            for (Object val : args){

                System.out.println(val);

            }
        }
        else{
            System.out.println("该方法没有传入实参");
        }

        return null;
    }
}
public class ProxyTest {

    public static void main(String[] args) {
        // 创建一个 InvocationHandler 对象
        InvocationHandler handler = new MyInvokationHandler();
        // 使用指定的 handler 来生成一个动态代理对象
        Persons p = (Persons) Proxy.newProxyInstance(Persons.class.getClassLoader()
        ,new Class[]{Persons.class},handler);
        // 调用动态代理的对象的 walk() 和 sayHello() 方法
        p.walk();
        p.sayHello("allen");
    }
}
```

生成结果：

``` java
------正在执行的方法：public abstract void Persons.walk()

该方法没有传入实参

------正在执行的方法：public abstract void Persons.sayHello(java.lang.String)

下面是执行该方法传入的实参为：

allen
```

从以上结果来看执行的都是 handler 中的 invoke() 方法。

## 三 . 反射和泛型

好好理解一下：Java 为 Class 类增加了泛型功能，从而允许使用泛型来限制 Class 类，如： **String.class** 的类型实际是 Class<String>.如果 Class 对应的类暂时未知，则使用 Class<?> .通过在反射中使用泛型，可以避免**因反射生成的对象需要强制类型转换**

* 泛型和Class类

下面这个简单的对象工厂，可以根据指定类来提供该类的实例。