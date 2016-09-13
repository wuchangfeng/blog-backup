---
title: 关于 LinkedHashMap 的内部实现机制
toc: true
date: 2016-05-24 07:44:24
tags: java
categories: About Java
description:
feature:
---

分析 LinkedHashMap 特点以及其实现机制，另外学习一下 LRU(近期最少使用算法) 算法。

<!--more-->

## 一 . 引入

先来看一下 LinkedHashMap 的实现用法例子：

``` java
public class LinkedHashMapTest {

    public static void main(String[] args) {
        Map<String,String> map = new LinkedHashMap<>(16,0.75f,true);
        map.put("1","a");
        map.put("2","b");
        map.put("3","c");
        map.put("4","d");
        map.forEach((key,value)-> System.out.println(key +"-->"+value));
        System.out.println("=========after get some values=========");
        map.get("1");
        map.get("2");
        map.forEach((key,value)-> System.out.println(key +"-->"+value));
    }
}
```

当 LinkedHashMap<>(16,0.75f,true) 时候，我们访问了 key 为 1 和 2 的元素。输出结果如下：

``` java
1-->a
2-->b
3-->c
4-->d
=========after get some values=========
3-->c
4-->d
1-->a
2-->b
```

当 LinkedHashMap<>(16,0.75f,false) 时候，我们同样访问了 key 为 1 和 2 的元素。输出结果如下：

``` java
1-->a
2-->b
3-->c
4-->d
=========after get some values=========
1-->a
2-->b
3-->c
4-->d
```

**小结**：当为参数为 true 时，会采用 LRU 来存放元素。而为 false 时，则按默认插入顺序输出元素。**即参数为 true 时，启用了 LinkedHashMap 内部的 LRU 特性**。

## 二 . LinkedHashMap 特性

1. 相对于 HashMap 的**无序**而言，LinkedHashMap 的输出会按照 **一定的顺序**。
2. LinkedHashMap 由于其自身的结构特点，非常适合于决定元素的快速访问与缓存淘汰问题。
3. LinkedHashMap 内部有一个环形双链表在维持元素的顺序,因此其遍历顺序相对 HashMap 来讲较慢。
4. LinkedHashMap 具有同版本 HashMap 的一些特性。如内部基本构造皆为:数组,链表,红黑树。

## 三 . 结构概览

### 3.1 基本结构

![LinkedHashMap.jpg](http://7xrl8j.com1.z0.glb.clouddn.com/LinkedHashMap.jpg)

如上图即为 LinkedHashMap 内部的基本构造。易见其在 HashMap 的基础上添加了一个双链表(黑色箭头)来维护其内部元素的顺序。

### 3.2 用法示意

![LinkedHashMap2.png](http://7xrl8j.com1.z0.glb.clouddn.com/LinkedHashMap2.png)

上图即访问了元素 3 之后，将其置于队列末端，形象的示意了 LinkedHashMap 内部工作机制的一种。

## 四 . 源码分析

### 4.1 继承关系

``` java
public class LinkedHashMap<K,V> extends HashMap<K,V> implements Map<K,V>
```
可以看到 LinkedHashMap 继承自 HashMap ，所以其可以共享 HashMap 中一些方法和成员变量。

### 4.2 相关属性

``` java
 // 双链表头结点	
 transient LinkedHashMap.Entry<K,V> head;
 // 双链表尾节点
 transient LinkedHashMap.Entry<K,V> tail;
 // 访问顺序	
 final boolean accessOrder;
```


### 4.3 构造函数

``` java
public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
}

public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
}

public LinkedHashMap() {
        super();
        accessOrder = false;
}

public LinkedHashMap(Map<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
		// 调用父类的 HashMap 函数
        putMapEntries(m, false);
}

public LinkedHashMap(int initialCapacity,
                         float loadFactor,
                         boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
 }
```

### 4.4 相关函数

**0.reinitialize()**

``` java
 // overrides of HashMap hook methods
void reinitialize() {
     super.reinitialize();
     head = tail = null;
}
```

**LinkedHashMap 初始化时建立循环列表**，reinitialize() 在 HashMap 中肯定声明了，且为 null。

**1.Entry<K,V>**

``` java
static class Entry<K,V> extends HashMap.Node<K,V> {
		// 每个节点包含 2 个指针，分别指向前面和后面的节点
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
}
```
我们把 HashMap 中的 Node<K,V> 挖出来看看：

``` java
static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
		// 这里定义的还是单链表，每个节点有一个指针，指向后面的节点
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
}
```

**2. NewNode()**

``` java
Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
            new LinkedHashMap.Entry<K,V>(hash, key, value, e);
		// 双链表尾部插入
        linkNodeLast(p);
        return p;
}
```

**3. NewTreeNode()**

``` java
TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
  		// 构造红黑树节点
        TreeNode<K,V> p = new TreeNode<K,V>(hash, key, value, next);
        linkNodeLast(p);
        return p;
}
```
当单链表中元素达到一定数量时，转化为**红黑树**，这个时候插入的就是红黑树节点了。注意 **linkNodeLast(p)。**

[红黑树的概念](http://baike.baidu.com/link?url=ZQNhI2Sace-2PNZQxrRCCsVv2M4RjEJJ6EVYG-5jTLVbi3kBVW9w0KeKW_beZyuxhf3i4ZjaN2lxgomVior16q)

**4. containsValue(Object value)**

``` java
 public boolean containsValue(Object value) {
        for (LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
			// 与 hashmap 一样，value 可以为 null
            if (v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
}
```
**map.containsValue("a")** 查找顺序按照插入顺序查找的

**5. afterNodeAccess(Node<K,V> e)**
``` java
 void afterNodeAccess(Node<K,V> e) {  
        LinkedHashMap.Entry<K,V> last;
		// 若访问顺序为true，且访问的对象不是尾结点
        if (accessOrder && (last = tail) != e) {
			// 向下转型，记录 p 的前后节点，分别为 a b
            LinkedHashMap.Entry<K,V> p =
                (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
			// 对于单链结构来说 p 的尾节点为空
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;// 从这里可以看出来双链表是环形的
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null) // 若最后一个节点为 null
                head = p;  
            else { // p 置于最后一个节点后面
                p.before = last;
                last.after = p;
            }
            tail = p;// 尾节点为 p
            ++modCount;
        }
 }
```

**move node to last：**如英文解释，当 选择访问顺序为 LRU 时，则将最近访问的节点置于双链表的尾部，核心操作还是双链表的移动节点操作。

**6.get(Object key)**

分析了 afterNodeAccess() 方法之后，可以看看 get() 方法的实现

``` java
public V get(Object key) {
        Node<K,V> e;
		// 通过HashMap的getEntry(Object key)方法获取节点
        if ((e = getNode(hash(key), key)) == null)
            return null;
		// 如果执行 LRU 的话，访问了该节点后，将该节点移动到链表末端
        if (accessOrder)
            afterNodeAccess(e);
		// 返回该数值
        return e.value;
}
```
**7. transferLinks(src,dst)**

``` java
// apply src's links to dst
private void transferLinks(LinkedHashMap.Entry<K,V> src,
                               LinkedHashMap.Entry<K,V> dst) {
        LinkedHashMap.Entry<K,V> b = dst.before = src.before;
        LinkedHashMap.Entry<K,V> a = dst.after = src.after;
        if (b == null)
            head = dst;
        else
            b.after = dst;
        if (a == null)
            tail = dst;
        else
            a.before = dst;
}
```

此函数用 dst 结点替换结点 src. **注意核心还是链表的插入和删除。**

小结:以上就是 LinkedHashMap 几个关键的函数。由于 LinkedHashMap 是基于 HashMap 的。所以只抽取其自身的一些特性来讲。

## 五 . 实现 LRU 算法

``` java
class LRULinkedHashMap<K,V> extends LinkedHashMap<K,V> {
    // 定义缓存的容量
    private int capacity;
    private static final long serialVersionUID = 1L;
    // 带参数的构造器
    LRULinkedHashMap(int capacity){
        // 调用LinkedHashMap的构造器
        super(16,0.75f,true);
        // 传入指定的缓存最大容量
        this.capacity=capacity;
    }
    // 一直在打印链表最顶端的元素
    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest){
        System.out.println(eldest.getKey() + "=" + eldest.getValue());
        return size()>capacity;
    }
}

public class LRUTEST {

    public static void main(String[] args) throws Exception{
        // 指定缓存最大容量为4
        Map<Integer,Integer> map=new LRULinkedHashMap<>(4);
      	map.put(8,5);
        map.put(4,2);
        map.put(3,1);
        map.put(5,4);
        map.put(9,6);
        // 总共put了5个元素，超过了指定的缓存最大容量
        // 遍历结果
        for(Iterator<Map.Entry<Integer,Integer>> it = map.entrySet().iterator(); it.hasNext();){
            System.out.println(it.next().getKey());
        }
    }
}
```

输出：

``` java
8=5
8=5
8=5
8=5
8=5
4
3
5
9
```

* 如上缓存定义容量为 4 ，但是最后 put 进入 5 个元素。从而输出时候会首先放弃最开始存储(最少接触)的元素即 key=8,value=5。
* 指定构造器中相应参数为 true。即开启了 LRU(近期最少使用算法) 特性。

## 六 . 参考引用 

* [LRU 近期最少使用算法](http://baike.baidu.com/link?url=oEk_OZr-G4PGRnLuIFDmzZvZh2zR-MuxQxg2W681k31vIL7v-5dGfBnuos6-BfCFDo9cO5z2i_FRLO9HNVk5uq)


* [JDK1.6 中的 LinkedHashMap](http://www.cnblogs.com/hzmark/archive/2012/12/26/LinkedHashMap.html)


* [图片引用出处](http://www.cnblogs.com/leesf456/p/5248868.html)