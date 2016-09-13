---
title: Java-TreeMap
date: 2016-04-22 15:05:10
tags: javacode
categories: About Java
---
java 源码分析-TreeMap

<!--more-->


下面这篇博客详细介绍了红黑树以及平衡二叉树，非常值得学习

[TreeMap](http://www.jianshu.com/p/210c2f4ca130#)

[TreeMap2](http://www.cnblogs.com/leesf456/p/5255370.html)

[TreeMap与TreeSet对比](http://blog.csdn.net/speedme/article/details/22661671)

[equals()和HashCode()](http://blog.csdn.net/speedme/article/details/22528047)


## 1. TreeMap 初识

其实现了 SortedMap 接口，TreeMap 其实就是一个红黑树结构，每个 key-value 都可以看做是一个节点，节点根据 **key** 进行排序。TreeMap 可以保证所有的 key-value 处于有序状态。两种排序方式：

* 自然排序：TreeMap 所有的 key 必须实现 **Comparable** 接口，而且所有的 key 都是同一个类的对象。
* 定制排序：创建 TreeMap 时，传入一个 **Comparator**对象，其负责对 TreeMap 中所有的 key
进行排序。


1. Map Entry firstEntry():返回 Map 中最小 key 所对应的 key-value 对，如 Map 为空，则返回 NUll
2. Object firstKey():返回 key 值最小
3. Map.Entry higherEntry(Object key):返回 Map 中位于 key 后一位的 key-value 键值对，即大小与 key 的最小的那个 key-value

以上几个例子，其实还有许多，主要因为 TreeMap 有序，所以会有访问第一个，前一个，后一个，最后一个的 key-value 对的方法，并提供了几个从 TreeMap 中截取子 TreeMap 的方法。


## 2. TreeMap 与 TreeSet

> 相同点：
TreeMap和TreeSet都是有序的集合，也就是说他们存储的值都是拍好序的。
TreeMap和TreeSet都是非同步集合，因此他们不能在多线程之间共享，不过可以使用方法Collections.synchroinzedMap()来实现同步
运行速度都要比Hash集合慢，他们内部对元素的操作时间复杂度为O(logN)，而HashMap/HashSet则为O(1)。

> 不同点：
最主要的区别就是TreeSet和TreeMap非别实现Set和Map接口
TreeSet只存储一个对象，而TreeMap存储两个对象Key和Value（仅仅key对象有序）
TreeSet中不能有重复对象，而TreeMap中可以存在


## 3. 二叉排序树

又称 二叉查找树（Binary Search Tree），也称有序二叉树（ordered binary tree）,排序二叉树（sorted binary tree），是指一棵空树或者具有下列性质的二叉树：

1. 若任意节点的左子树不空，则左子树上所有结点的值均小于它的根结点的值

2. 若任意节点的右子树不空，则右子树上所有结点的值均大于它的根结点的值

3. 任意节点的左、右子树也分别为二叉查找树

4. 没有键值相等的节点（no duplicate nodes）

如上定义，最好的情况就是一棵树左右节点数目差不多，即平衡状态，但是在不断插入新节点的过程中，非常都容易导致一边倒的情况，即有可能左边插入的节点非常多，或者右边。为此出现了：AVL，SBT，伸展树，TREAP ，红黑树 来维持平衡状态的算法。

而对于平衡二叉树：要求对于任意节点，其左右子树的高度差不超过1，并且左右子树都要满足此性质

## 4. 红黑树

红黑树通过以下性质来维持二叉树插入过程中的平衡：

1. 每个节点都只能是红色或者黑色
2. 根节点是黑色
3. 每个叶节点（NIL节点，空节点）是黑色的。
4. 如果一个结点是红的，则它两个子节点都是黑的。也就是说在一条路径上不能出现相邻的两个红色结点。
5. 从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点

在插入新节点的过程中，不停的进行**左旋**，**右旋**，**着色**的过程，这些都是为了维持平衡

![](http://7xrl8j.com1.z0.glb.clouddn.com/450px-Red-black_tree_example.svg.png)


## 5. TreeMap APITest

``` java
	public class TreeMapTest {

    public static void main(String[] args) {

        CommonApiTest();
        System.out.println("======================");
        NavigableMapAPITest();
    }

    public static void CommonApiTest(){
        
        Random r = new Random();
        TreeMap treeMap = new TreeMap();
        treeMap.put("a",r.nextInt(10));
        treeMap.put("b",r.nextInt(10));
        treeMap.put("c",r.nextInt(10));
        //输出TreeMap
        System.out.println(treeMap);
        //Iterator 遍历 键值对
        Iterator itr = treeMap.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry =(Map.Entry)itr.next();
            System.out.println(entry.getKey()+ ","+entry.getValue());
        }
        //treeMap 键值对个数
        System.out.println(treeMap.size());
        //是否包含指定 key，指定value
        System.out.println(treeMap.containsKey("one"));
        System.out.println(treeMap.containsValue(r.nextInt(10)));
        //删除指定 key 的键值对
        treeMap.remove("a");
        System.out.println(treeMap);
        //清空treemap
        treeMap.clear();
        System.out.println(treeMap.isEmpty());
    }

    public static void NavigableMapAPITest(){
        // 新建TreeMap
        NavigableMap nav = new TreeMap();
        // 添加“键值对”
        nav.put("aaa", 111);
        nav.put("bbb", 222);
        nav.put("eee", 333);
        nav.put("ccc", 555);
        nav.put("ddd", 444);
        // 打印出TreeMap
        System.out.println(nav);
        // 获取第一个key、第一个Entry,在排好序的情况下
        System.out.println(nav.firstKey()+ ","+nav.firstEntry());
        // 获取最后一个key、最后一个Entry
        System.out.println(nav.lastKey() +","+nav.lastEntry());
        // 获取“小于/等于bbb”的最大键值对
        System.out.println(nav.floorKey("bbb"));
        // 获取“小于bbb”的最大键值对
        System.out.println(nav.lowerKey("bbb"));
        // 获取“大于/等于bbb”的最小键值对
        System.out.println(nav.ceilingKey("ccc"));
        // 获取“大于bbb”的最小键值对
        System.out.println(nav.higherKey("ccc"));
        }
    }
```



## 6. TreeMap 源码分析

### 6.1. 常量定义

``` java
	public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable{
    //比较器
    private final Comparator<? super K> comparator;
    //根节点
    private transient Entry<K,V> root;
	//节点的数量
    private transient int size = 0;
	//对TreeMap操作次数,影响 fail-fast
    private transient int modCount = 0;
    }
```

NavigableMap是JDK1.6新增的，在SortedMap的基础上，增加了一些“导航方法”（navigation methods）来返回与搜索目标最近的元素。例如下面这些方法：

1. lowerEntry，返回所有比给定Map.Entry小的元素
2. floorEntry，返回所有比给定Map.Entry小或相等的元素
3. ceilingEntry，返回所有比给定Map.Entry大或相等的元素
4. higherEntry，返回所有比给定Map.Entry大的元素

### 6.2. 构造方法

``` java
	  public TreeMap() {
        comparator = null;
       }
     //制定了比较器，即定制排序
     public TreeMap(Comparator<? super K> comparator) {
         this.comparator = comparator;
      }
     //指定了 map
     public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
     }
 
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }
```

### 6.3. Entry 定义(即红黑树节点定义)

``` java
	    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left; // 左孩子节点
        Entry<K,V> right;//右孩子
        Entry<K,V> parent;//父节点
        boolean color = BLACK;//节点颜色，默认黑色
        
        //初始化一个节点
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        //返回key
        public K getKey() {
            return key;
        }

        //返回value
        public V getValue() {
            return value;
        }

        //重置节点的value并返回旧value
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        //用自己的一套，重写 equals 和 hashcode 
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }
        // map 重写 toString ，所以sout(map)会输出key+value
        public String toString() {
            return key + "=" + value;
        }
    }
```


