---
title: Java-ArrayList
date: 2016-04-13 21:37:48
tags: javacode
categories: About Java
---
java 源码分析-ArrayList

<!--more-->

## 1. 初识 ArrayList


ArrayList 和 vector 都是基于数组实现的 List 类，所以它们两封装了一个动态的，允许再分配的 Object[] 数组。Arraylist/vector 使用 initialCapacity 参数来设置数组长度，当向集合中添加元素的数目超过长度时，initialCapacity 会自动增加。

最开始，可以使用 ensureCapacity(int minCapacity) 方法初始化，一次性增加，这样可以减少重新分配的次数，提高性能。不指定的话，默认是10.

Arraylist/vector 提供了以下两个方法来重新分配 Object[] 数组。

* void ensureCapcity(int minCapacity):将 ArrayList或者Vector集合的 Object[]数组长度增加大于或等于 minCapcity
* vid trimToSize():调整ArrayList或者Vector集合的 Object[]数组长度为当前元素的个数，调用该方法，可以减少两个集合对象所占用的空间。


## 2. 源码分析

1 ArrayList定义：

``` java

	public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

从定义可见，其支持泛型，实现了 List，RandomAccess，Cloneabele，java.io.Serializable 等接口，其中 RandomAccess 就是表明 ArrayList 支持快速随机访问，为标记接口，大部分list 集合都实现了该接口，linkedlist 未实现，其效率有明显差异。[Cloneable接口](http://kentkwan.iteye.com/blog/739514) 可以调 Object.clone方法返回该对象的**浅拷贝**

2 ArrayList属性定义
  
``` java

    // 序列化时所需要的标示
	private static final long serialVersionUID = 8683452581122892189L;
    //默认的存储容量
    private static final int DEFAULT_CAPACITY = 10;
    // 为第一个构造函数 准备好数组对象
    private static final Object[] EMPTY_ELEMENTDATA = {};
    // 为第二个构造函数所准备
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    // elementData存储ArrayList内的元素，但是在序列化过程中，elementData 不参与
    transient Object[] elementData; 
    // 包含元素的数量
    private int size;
```

3 ArrayList的构造方法

``` java

	  public ArrayList(int initialCapacity) {
        // 一旦指定大小了之后，用initialCapacity来初始化数组的大小
        if (initialCapacity > 0) {  
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
		// 没有指定大小，则用系统默认的
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
    // 构造一个空的list 数组，其初始值为10
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    // 将提供的集合转化成数组赋给 elementData
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
             // 并且返回若不是 objetc[],则调用.copyOf强转
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
```

4. ArrayList 方法实现

4.1	add(e)
     
``` java

	public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
		// 赋值完毕后，size 自增1
        elementData[size++] = e;
        return true;
    }
```

为在 ArrayList 最后添加一个元素，设想如何添加？数组如何变化，一旦容量不够，如何进行扩容？

首先 我们进入  ensureCapacityInternal(size + 1) 去看看，它肯定是确保容量的。

``` java

    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private void ensureExplicitCapacity(int minCapacity) {
        // 记录list结构被改变的次数，但是底下这个 if 表示不一定会被改变啊
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
	//
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    //
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
		// oldCapacity 右移一位，其效果相当于oldCapacity /2，我们知道位运算的速度远远快于整除运算，整句运算式的结果就是将新容量更新为旧容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

* 首先得到数组的旧容量，然后进行oldCapacity + (oldCapacity >> 1)，将oldCapacity 右移一位，其效果相当于oldCapacity /2，我们知道**位运算的速度远远快于整除运算**，整句运算式的结果就是将新容量更新为旧容量的1.5倍

* 然后检查新容量是否大于最小需要容量，若还是小于最小需要容量，那么就把最小需要容量当作数组的新容量，

* 接着，再检查新容量是否超出了ArrayList所定义的最大容量，若超出了，则调用hugeCapacity()来比较minCapacity和MAX_ARRAY_SIZE，如果minCapacity大于最大容量，则新容量则为ArrayList定义的最大容量，否则，新容量大小则为minCapacity。 

* 还有一点需要注意的是，容量拓展，是创建一个新的数组，然后将旧数组上的数组copy到新数组，这是一个很大的消耗，所以在我们使用ArrayList时，最好能预计数据的大小，在第一次创建时就申请够内存。


注意 jdk1.6 和 jdk 1.7 最大的区别就是这里了。


4.2 clear()

``` java

	  public void clear() {
        modCount++;

        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }
```

注意 clear的时候并没有修改elementData的长度


4.3 clone()

``` java

	 public Object clone() {
        try {
            ArrayList<?> v = (ArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
```

这里只是浅克隆 ，返回 ArrayList 实例的副本.也可以看到 modCount 也被修改为 0


4.4 indexof()

``` java

	public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
```

返回对象 o 第一次在 list 中出现的位置，如果不存在则返回 -1，因为 ArrayList 允许元素为null，故也可以返回 null 的位置。


4.5 toArray()

``` java

	public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }
```

将 ArrayList 以数组形式返回。Arrays 是一个数组工具类。


4.6 trimToSize()

``` java

	public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
              ? EMPTY_ELEMENTDATA
              : Arrays.copyOf(elementData, size);
        }
    }
```

elementData 会动态扩容啊，所以有可能 elementData 很大，但是 size 个数很小，所以需要去清空这些没用的空间。

size == 0 为真，则 elementData = EMPTY_ELEMENTDATA,

否则 elementData = Arrays.copyOf(elementData, size);

4.7 set(int index, E element)

``` java

	 public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
```

简单，先判断索引是否越界，没有的话先把原来位置上的元素取出，再放上新的元素，最后返回原来位置的元素。

4.8 private transient Object[] elementData;

上面这个对象数组就是其存储元素的数据结构，前面有一个java关键字transient，这个关键字是去序列化的意思，即，在这个类序列化后保存到磁盘或者输出到输出流的时候，这个对象数组是不被保存或者输出的。

这里又有疑问了，这个数组不是存储我们保存的数据的吗？为什么要去序列化呢？那么如果去掉序列化之后，我们保存的元素从哪里来呢？ 其实问题就是**为什么用transient关键字来修饰 Object[] elementData？**

这就跟这个ArrayList的特性有关，我们知道ArrayList的容量，也就是这个数组的容量，一般都是预留一些容量，等到容量不够时再拓展，那么就会出现容量还有**冗余**的情况，如果这时候进行序列化，整个数组都会被序列化，连后面没有意义空元素的也被序列化。**这些是不应该被存储的**。所以java的设计者，就为这个**类提供了一个writeObject方法，在实现了Serializable接口的类**，如果这个类提供了writeObject方法，那么在进行序列化的时候就会通过writeObject方法进行序列化，所以ArrayList的writeObject方法就会显式的为每个实际的数组元素进行序列化，只序列化有用的元素。

``` java
	
	private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // modCount 数值，我们每修改一次 Arraylist 结构都会变一次
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out size as capacity for behavioural compatibility with clone()
        s.writeInt(size);

        // 序列化实际的元素，而那么空位置不能滥竽充数
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
        // ArrayList 是线程不安全的啊~，如果在别的线程中修改了，与预期不符合
		// 就抛出异常
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
```
如上自定义序列化写入方式

另外 序列化有2种方式： 

A、只是实现了Serializable接口。 

  序列化时，调用java.io.ObjectOutputStream的defaultWriteObject方法，将对象序列化。 

注意：此时transient修饰的字段，不会被序列化。 

B、实现了Serializable接口，同时提供了writeObject方法。 

  序列化时，会调用该类的writeObject方法。而不是java.io.ObjectOutputStream的defaultWriteObject方法。 

注意：此时transient修饰的字段，是否会被序列化，取决于writeObject。 


**总之** 用transient来修饰并不代表不可以序列化该数组，只是我们想重写 writeObject(),从而**自定义序列化**。


具体的可以参见 java IO。

## 3. modCount 关键字

突然发现 protected transient int modCount = 0 其修饰符为 protected 并且用 transient 关键字修饰了，发现有点名堂。

简而言之，modCount 关键字用来保证集合安全的，与其经常比较的一个 变量就是 expectedModCount，modCount 来自 AbstractList 为全局变量，而 expectedModCount 是不会变的，这样一旦多个线程对集合进行操作，modCount 会改变，从而导致两个数值不相等，就会产生异常即 fail-fast。

这块还是不太明白，这个异常跟迭代关系很大，迭代的过程中，对数据元素的增加或者减少都有可能影响 fail-fast吗？

我初步知道fail-fast产生的原因就在于程序在对 collection 进行迭代时，某个线程对该 collection 在结构上对其做了修改，这时迭代器就会抛出 ConcurrentModificationException 异常信息，从而产生 fail-fast。


[关于fail-fast](http://blog.csdn.net/chenssy/article/details/38151189)

## 3. 总结

ArrayList 还是比较简单的，其中一个核心的概念就是动态扩容，以及 ArrayList 支持 null，并且

元素有序，每个元素都有其对应的索引位置。

* java 位运算不熟练

* java 克隆对象

[Arraylist 源码分析](http://www.tuicool.com/articles/Evu2IzF)