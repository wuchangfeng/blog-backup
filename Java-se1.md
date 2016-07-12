---
title: Java 常用集合
date: 2015-09-15 20:38:18
tags: java
categories: About Java
---

读书笔记之 java 常用集合


![](http://7xrl8j.com1.z0.glb.clouddn.com/java%E9%9B%86%E5%90%88.jpg)

<!-- more -->



### 1. 集合

* set代表**无序不可重复***的集合

* List代表**有序可以重复**的集合

* Map代表具有**关系映射**的集合

* Queue代表**队列**集合 

### 2. Collection 和 iterator 接口

``` java
public class CollectionTest {

    public static void main(String args[]){

        Collection c = new ArrayList<>();
        // 虽然集合中不能有基本元素，但是 java支持自动装箱
        c.add("ABC");
        c.add(6);
        System.out.println("C 中元素个数： " + c.size());
        c.remove(6);

        System.out.println("C 包含ABC" + c.contains("ABC"));
        c.add("BNM");
        System.out.println(c);

        Collection books = new HashSet<>();
        books.add("book1");
        books.add("book2");
        System.out.println("c 包含 books " + c.containsAll(books));
    	}
}
```
### 3. 通用

#### 使用Lambda表达式遍历集合

java 8 为 Iterator 新增了一个 forEach 默认方法，其所需参数类型是一个函数式接口，而 Iterator 接口 是 collection 接口的父接口，因此 几种集合都可以直接调用该方法。

``` java
public class CollectionEach {

    public static void main(String args[]){

        Collection books = new HashSet();

        books.add("book1");
        books.add("book2");
        books.add("book3");

        books.forEach(obj->System.out.println("元素迭代 ：" + obj));
    	}
}
```

#### 使用 Java8 增强的Iterator 遍历集合

Iterator 也是集合框架中的成员，但是与其他不同的是他是专门用来遍历集合中元素的接口。
该接口定义了以下4个方法：

* boolean hasNext():
* Object next():
* void remove():
* void forEachRemaining(Consumer action):



#### 使用 foreach 循环遍历集合

这种方法非常简单，是 java5 提供的。即

	
	for(Object obj : books)

#### 使用 java 8 新增的 Predicate 遍历集合

java 8 为集合 Collection 新增了一个 remove(Predicate filter) ,会批量删除符合 filter 条件的元素。

#### java 8 新增的 Stream 操作集合

流式API，好处是什么？



### 4. Set(不允许重复的元素)

#### HashSet

哈希的价值在与其速度和查找性能。

* 不能保证元素的额排列顺序,顺序可能与添加顺序不同，发生变化
* Hashset 不是同步的，如果有多个 线程来访问一个 HashSet，必须通过代码来保证它们的同步。
* 集合元素值可以是 null

当向 HashSet 集合中存入一个元素时，Hashset 先会调用该对象的hashcode()来获得hashcode值，根据该值决定其在hashset中的位置。

如果两个元素通过 equals() 比较 true，但是他们的 hashCode() 方法返回值不相等，**HashSet**将会把它们存储在不同的位置

注意：HashSet 判断两个元素相等的标准是判断两个对象通过equals方法比较相等，并且两个对象的hashcode()也要相等。

``` java
// A 的equals() 总是返回true，但是没有重新起 hashcode()
class A{

    	public boolean equals(Object obj){
    	    return true;
    	}
}
//类 B 的hashcode() 总是返回1，但是没有重写它的equals()
class B{

    	public int hashCode(){
    	    return 1;
    	}
}
//类 C 的hashCode() 和 equals() 都重写了
class C{
    	@Override
    	public int hashCode() {
        	return 2;
}

    @Override
    public boolean equals(Object obj) {
        return true;
    	}
	}
//测试类
public class HashSetTest {

    	public static void main(String[] args) {

        HashSet books = new HashSet();
        books.add(new A());
        books.add(new A());
        books.add(new B());
        books.add(new B());
        books.add(new C());
        books.add(new C());

        System.out.println(books);
    	}
}
```

输出结果：

	[B@1, B@1, C@2, A@677327b6, A@1540e19d]

规则是：重写了该对象对应类的 equals() 方法返回 true，这两个对象的 HashCode() 也应该相同。

* 如果两个对象的 equals() 相等，表示两个对象的数值相同，而 hashCOde 不相同，这将导致 HashSet 将两个放在 Hash 表的不同位置，但是这还是不能添加成功的啊，因为 **Set不允许重复的元素**

* 如果两个对象的 hashCode() 相同，但是 equals() 比较却返回 false ，这时候很麻烦， Hash 表将采用链式结构来存储这些元素，而 HashSet 是根据 hash 数值来访问元素的，这样就不可避免的降低了 hash 表的效率

所以 HashCode() 对于 HashSet，HashMap 都很重要，计算的过程中，要保证 hash 数值在有限的条件下，发挥最大的效率。

下面给出重写 HashCode() 的一般步骤：

1. 把对象内每个有意义的实例变量(即参与 equals 比较的标准实例变量)计算出一个 int 类型的 hashCode() 数值。

  * boolean ： hashCode = (f?0:1)
  * 正数类型(byte,short,int,char)：hashCode = (int)f
  * long： hashCode = (int)(f^(f>>>32))
  * float：
  * double：
  * 引用类型：hashCode = f.hashCode()

2. 用第一步计算出的多个 hashCode() 数值组合出一个新的 HashCode() 数值返回：

	return f1.hashCode() + (int) f2;

为了避免直接相加产生的偶然相等(两个对象的 f1，f2 实例变量并不相等，但是他们 hashCode() 的和恰好相等)，可以通过各实例变量的 hashCode() 乘以任意一个质数再相加，如下：

	return f1.hashCode() * 19 + (int)f2 * 31

3. 第二步计算的结果 就是一个对象的 HashCode 数值，可以看出计算出一个实例变量的 HashCode()
要考虑的就是对象内部的实例变量





#### LinkedHashSet

这个是 HashSet的子类，其也是根据元素的 HashCode 值来决定它的存储位置，但是它同时使用**链表维护元素的次序**，使其看起来像是以插入的顺序保存的，也就是说当遍历 LinkedHashSet 集合中的元素时，LinkedHashset 将会按照往集合里添加元素的顺序访问元素。

正式由于要维护元素的顺序，所以其**性能略差一点**，但是迭代访问时有很好的效率。


上述两个程序如下：

``` java
public class LinkedHashSetTest {
    public static void main(String args[]){
        LinkedHashSet book = new LinkedHashSet();

        book.add("A");
        book.add("B");
        System.out.println(book);
        book.remove("A");
        book.add("C");
        System.out.println(book);
        System.out.println("===========");

        HashSet book1 = new HashSet();
        book1.add("F");
        book1.add("M");
        book1.add("L");
        System.out.println(book1);
        book1.remove("M");
        book1.add("P");
        System.out.println(book1);
    }
}
```
结果：

	[A, B]
	[B, C]
	[F, L, M]
	[P, F, L]

由此可见 LinkedList 集合元素，元素顺序总是与添加顺序一致。

#### TreeSet

TreeSet 是 SortedSet接口的实现类，其可以确保集合中的元素位置，它是根据元素大小来排序的，而不是插入时顺序。TreeSet 额外提供的方法如下代码中所示。

与hashset采用**hash算法来决定元素存储位置不同**，treeset采用**红黑树的数据结构**来存储集合元素。其支持两种排序规则：自然排序和定制排序。

``` java
public class TreeSetTest {

    public static void main(String args[]){

        TreeSet num = new TreeSet();
        num.add(5);
        num.add(2);
        num.add(3);
        num.add(-9);
        //[-9, 2, 3, 5] 集合处于排序状态
        System.out.println(num);
        //-9
        System.out.println(num.first());
        // 5
        System.out.println(num.last());
        //返回小于3的子集，不包含3 [-9,2]
        System.out.println(num.headSet(3));
        //返回大于5的子集，包含5
        System.out.println(num.tailSet(5));
        //返回大于等于2 小于5的子集
        System.out.println(num.subSet(3,5));
    }
}
```

总结：
TreeSet 最后输出是根据元素实际大小来排序的，不是根据插入时顺序。

TreeSet进行排序规则默认是自然排序。

自然排序：

TreeSet会调用集合的 compareTo(Object obj)方法来比较元素之间大小关系，然后将集合元素按升序排列，即自然排序。

java 中： BigDecimal，Character，Boolean，String，Date，Time 等都实现了 Comparable 接口。故而把一个对象添加进入 TreeSet，则必须实现 Comparable 接口，否则出错。

另外想 TreeSet 中添加元素时，只需第一个元素无需实现 Comparable，后面添加的都必须实现。添加进入 TreeSet 肯定是要同一类型啊，负责就懵逼了。

**treeSet 判断两个元素相等的标准是什么？**


一小段代码献上：

```java
class Z implements Comparable{
    int age;

    public Z(int age) {
        this.age = age;
    }
    //总是返回一，则总是认为两个元素不相等，故而可以添加
    @Override
    public int compareTo(Object o) {
        return 1;
    }
    @Override
    public boolean equals(Object obj) {
        return true;
    	}
	}

  	public class TreeSetTest2 {

    public static void main(String[] args) {

        TreeSet set = new TreeSet();
        Z z1 = new Z(6);
        set.add(z1);
        set.add(z1);
        System.out.println(set);
        System.out.println();
        System.out.println(z1.age);
    	}
}
```
如上判断两个对象相等的标准就是  compareTo(Object o) 方法，要是返回 0 就认为它们相等，否则不相等。


定制排序：

实现非自然排序，则需要通过 Comparator 接口的帮助。

``` java 
class M{
    int age;

    public M(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "M[age:" + age + "]";
    	}
	}

	public class TreeSetTest3 {

    public static void main(String[] args) {
        
        TreeSet ts = new TreeSet(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                M m1 = (M)o1;
                M m2 = (M)o2;
                return m1.age > m2.age ? -1
                        :m1.age < m2.age ? 1:0;
            }
        });
        ts.add(new M(5));
        ts.add(new M(-1));
        ts.add(new M(9));
        System.out.println(ts);
    	}
}
```

#### EnumSet

EnumSet 内部是以位向量的形式存储，这种方式非常紧凑，效率非常高，有序的哦，不能添加null啊



### 5. List

#### Java 8 改进的List接口和ListIterator接口

List 作为 collection 接口的子接口，当然可以使用 Collection 接口的全部方法，此外由于其是**有序**集合，所以其添加了一些根据索引来操作集合元素的方法。List集合常用方法如下图：

``` java
public static void main(String args[]){

        List books = new ArrayList();
        //新字符串插入
        books.add(new String("book1"));
        books.add(new String("book2"));
        books.add(new String("book3"));
        System.out.println(books);
        // 新字符串插入第二个位置
        books.add(1,new String("book4"));
        for (int i=0;i<books.size();i++){
            System.out.println(books.get(i));
        }
        books.remove(2);
        System.out.println(books);
        //判定元素在List中的位置
        System.out.println(books.indexOf(new String("book2")));
        books.set(1,new String("book5"));
        System.out.println(books);
        System.out.println(books.subList(1,2));
}
```

**List 判断两个对象相等，只需要通过equals()方法即可**。对比可以想一下 hashset.

``` java
	class G {

    public boolean equals(){
        return true;
		    }
	}
	public class ListTest2 {

      public static void main(String args[]){
        List books = new ArrayList();

        books.add(new String("book1"));
        books.add(new String("book2"));
        books.add(new String("book3"));
        System.out.println(books);

        books.remove(new A());
        System.out.println(books);
        books.remove(new A());
        System.out.println(books);
	    }
}
```

输出：

	[book1, book2, book3]
	[book2, book3]
	[book3]

能想想为什么吗？ 当程序想要删除 A 对象时，List 将会调用 A对象的 equals() 方法与几何中元素依次，由于重写了 equals() 方法，会认为元素相等。故删除。

#### ArrayList 和 Vector 实现类

[疑问点](http://www.jianshu.com/p/0f3c788adb84)

Vector是一个古老的框架，方法名字非常长，而 ArrayList 开始就作为 List 的主要实现类，实际上 Vector有很多缺点，少用。

ArrayList 和 Vector 最大区别：ArrayList 是线程不安全的，Vector 是安全的，多个程序访问集合时，后者无需保证集合的同步性，但也由此后者的效率偏低。尽管如此，我们还是会选择 ArrayList，存在一个 工具类保证 ArrayList 线程安全。

#### LinkedList实现
 
linkedList类是List接口实现类，意味着他是一个List集合，可以根据索引访问集合中的元素，除此之外它还实现了Deque接口，可以被当成双端队列来使用，因此既可以被当成栈使用，又可以被当成队列来使用。

``` java
public class LinkedListTest {

    public static void main(String args[]){
        LinkedList books = new LinkedList();
        //将字符串加入队列w尾部
        books.offer("book1");
        //将一个字符串加入栈的顶部
        books.push("book2");
        //将字符串元素添加到队列的头部(相当于栈的顶部)
        books.offerFirst("book3");
        //以List方式来遍历集合，相当于按照索引
        for (int i = 0;i < books.size();i++){
            System.out.println("遍历中： " + books.get(i));
        }
        //访问并不删除栈顶的元素
        System.out.println(books.peekFirst());
        //访问并不删除队列的最后一个元素
        System.out.println(books.peekLast());
        //将栈顶的元素弹出"栈"
        System.out.println(books.pop());
        //下面将看到队列第一个元素被删除
        System.out.println(books);
        //访问并删除队列的最后一个元素
        System.out.println(books.pollLast());

        System.out.println(books);
    }
}
```



* ArrayList 和 ArrayDeque 内部以数组的形式来保存集合中的元素，因此随机访问数组时，有较好的性能。
* LinkedList 内部以链表的形式来保存集合中的元素，因此随机访问时，性能较差，但是在插入和删除数据时性能比较出色，只需要改变指针所指地址即可。 

总结分析：

只需要知道 LinkedList集合不仅提供了List的功能，还提供了 栈，双端队列的功能。由于数组以一块连续内存来保存所有的数组元素，所以数组在随机访问时性能最好，所有的内部以数组作为底层实现的集合，在随机访问时，效率都比较好；而内部以链表作为底层实现的集合在执行插入，删除操作有较好的性能，但是总体来说 Arraylist 性能比 Linkedlist 性能要好。

另外如果要遍历 List 集合中元素，对于 ArrayList和 vector 使用get，而对于 LinkedList 则使用迭代器(Iterator)最好。

如果需要经常插入和删除包含大量数据的List集合的大小，可以考虑使用LinkedList集合。使用 ArrayList和Vector集合可能需要经常重新分配内部数组大小，效果可能更差。

如果多个线程需要同时访问List集合中的元素，开发者可以使用Collections将集合包装成线程安全集合。



#### 固定长度的List

关于数组时，有一个操作数组的工具类：Arrays，该工具提供了 asList(object...a)方法,该方法可以把一个数组或者指定个数的对象转换成一个List集合，这个集合不是 Vector 和 ArrayList 的实例，而是 Arrays 内部类 ArrayList 内部类的实例。只能遍历集合元素，不能修改。

``` java
public class FixedSizeListTest {

    public static void main(String args[]){

        //List fixedlist = Arrays.asList("book1","book2");
        List fixedlist = Arrays.asList("book1","book2");
        //获取fixedlist 的实现类
        System.out.println(fixedlist.getClass());
        // 遍历
        fixedlist.forEach(System.out::println);

        //都会报异常
        fixedlist.add("book3");
        fixedlist.remove("book2");
    }
}
```

### 6. Queue集合

用于模拟队列这种数据结构，先进先出啊，Queue 接口定义的方法：

* void add(Object o):
* Object element():获得队列头部的元素，并不是删除
* Object peek():同上
* Object poll():同上，但是会删除该元素。
* Object remove():获取队列头部元素，并删除。


#### PriorityQueue 实现类

#### Dequen接口与ArrayDeque 实现类

定义了一些双端队列方法。Deque 接口提供了 ArrayDeque 实现类，其基于数组实现的双端队列，默认底层数组长度为16，其底层实现机制与 ArrayList 基本相似。

ArrayDeque 当栈来用：

``` java
public class ArrayDequeStackTest {

    public static void main(String[] args) {

        ArrayDeque stack = new ArrayDeque();

        stack.push("book1");
        stack.push("book2");
        stack.push("book3");
        System.out.println(stack);
        //访问不弹出
        System.out.println(stack.peek());
        System.out.println(stack);
        //访问弹出
        System.out.println(stack.pop());
        System.out.println(stack);
    }
}
```

ArrayDeque 当双端队列来用：

``` java
public class ArrayDequeQueueTest {

    public static void main(String[] args) {

        ArrayDeque queue = new ArrayDeque();
        queue.offer("book1");
        queue.offer("book2");
        queue.offer("book3");
        System.out.println(queue);
        //访问队列头部的元素，但是并不将其 poll 出队列“栈”
        System.out.println(queue.peek());
        System.out.println(queue);
        //poll出第一个元素
        System.out.println(queue.poll());
        System.out.println(queue);
    	}
}
```

#### LinkedList 实现类

参见 java 源码分析之 LinkedList


### 7. Map

Map 用于保存具有映射关系的数据。Map的key不允许重复。但是 Value 之间可以，非常类似于一个 List。Map集合最典型的用法就是成对的添加，删除key-value对，接下来即可判断该Map中是否包含指定key，指定value，也可以通过Map提供的KeySet()获得所有的key组合，进而遍历所有的键值对。实例如下：
	
``` java
public class MapTest {

    public static void main(String args[]){
       // Map hash = new HashMap();

        Map map = new HashMap();
        map.put("a",12);
        map.put("b",13);
        map.put("c",14);
        //覆盖了原有的value，则该方法返回被覆盖的value
        System.out.println(map.put("a",13));
        System.out.println(map);

        System.out.println("是否包含c：" + map.containsKey("c"));
        System.out.println("是否包含值为13：" + map.containsValue(13));

        for (Object key : map.keySet()){
            System.out.println(key + " -->" + map.get(key));
        }
        map.remove("c");
        System.out.println(map);
    }
}
```

#### HashMap 和 Hashtable

两者之间的关系完全类似于 ArrayList 和 Vector，Hashtable 是一个比较古老的map实现类。名字都没有遵循 Java 命名规范。
两者之间区别如下：

* hashtable 是一个线程安全的 Map实现类，HashMap线程不安全，所以 **HashMap的性能较高一点**。但是如果多个线程访问一个 Map 对象，使用 Hashtable 效果肯定更好一点。
* Hashtable 不允许使用 null 作为key和value，如果试图把null值放进Hashtable中，将会引发nullpointer异常，但HashMap可以使用null作为key或者value。

实例一：

``` java
public class NullInHashMap {
    public static void main(String args[]){

        HashMap hm = new HashMap();

        hm.put(null,null);
        hm.put(null,null);
        hm.put("a",null);

        System.out.println(hm);
    	}
}
```

结果：
	{null=null, a=null}


HashMap 和 Hashtable 也不能保持元素对的顺序，类似与 HashSet 这两个集合判断元素相等的标准分为key和value。

key 通过equals()相等即可，Value则判断返回true则相等。


#### LinkedHashMap 实现类

HashSet 有一个  LinkedHashSet 子类，HashMap 也有一个 LinkedHashMap 子类；LinkedHashMap 也使用**双向链表来维护 key-value 的顺序**。该链表负责维护 Map 的迭代顺序。其性能由于需要链表来维护顺序，自然略低于 HashMap。能记住 key-value 添加的顺序，输出时也一样。


``` java
public class LinkedHashMapTest {


    public static void main(String args[]){

        LinkedHashMap lhp = new LinkedHashMap();
        lhp.put("a",1);
        lhp.put("b",2);
        lhp.put("c",3);
        
        lhp.forEach((key,value)->System.out.println(key + "-->" + value));

    	}
}
```

输出
	a-->1
	b-->2
	c-->3


#### 使用 Properties 读写属性文件

其为 Hashtable 子类，其处理属性文件特别方便，Properties 可以把 Map 对象和 属性文件关联起来，从而可以将 Map 中的 key-value 写入文件。总之 Properties 相当于一个 key，vlaue 都是 String 类型的 Map。实例代码如下哦：

``` java
public class PropertiesTest {

    public static void main(String[] args) throws Exception{

        Properties pro = new Properties();
        //添加属性
        pro.setProperty("usename","gang");
        pro.setProperty("password","123456");
        //key-value 保存到 a.ini 文件中
        pro.store(new FileOutputStream("a.ini"),"comment line");
        //将 a.ini 的内容追加到 pro2 中
        Properties pro2 = new Properties();
        pro2.setProperty("gender","male");
        pro2.load(new FileInputStream("a.ini"));
        System.out.println(pro2);
   	 }
}
```

#### SortedMap接口和TreeMap 实现类

这里首先最重要的就是学到了一个"实体Entry"的概念，在 HashMap 源码中，到处都充斥着这个字眼==

TreeMap 就是一种红黑树结构，每个 key-value 作为红黑树的一个节点，TreeMap 会根据 key 来对节点进行排序，可以保证所有的 key-value 处于有序状态。

自然排序：

定制排序：


####　WeakHashMap实现类

HashMap 的 key 保持了对实际对象的强引用，主要 HashMap 对象不被销毁，key 所引用的实际对象就不会销毁，但 weakhashMap 的 key 只保留了对实际对象的弱引用，一切与 HashMap 完全相反。

实例如下：

``` java
public class WeakHashMapTest {

    public static void main(String args[]){

        WeakHashMap whm = new WeakHashMap();
        //匿名字符串对象，weakhashmap只保留了对它们的弱引用
        whm.put(new String("语文"),new String("A"));
        whm.put(new String("数学"),new String("B"));
        whm.put(new String("英语"),new String("C"));
		//字符串直接拿量，系统会保持对他的强引用
        whm.put("Java",new String("B"));

        System.out.println(whm);

        System.gc();
        System.runFinalization();
        System.out.println(whm);

    	}
}
```

结果：

	{Java=B, 数学=B, 英语=C, 语文=A}
	{Java=B}


#### identityHashMap实现类

IdentityHashMap 提供了与 HashMap 基本类似的方法，允许使用 null 作为 key 和 value。也不能保证元素的添加顺序。

``` java
public class IndentityHashMapTest {

    public static void main(String args[]){

        IdentityHashMap ihm = new IdentityHashMap();

        ihm.put(new String("语文"),89);
        ihm.put(new String("语文"),90);

        ihm.put("java",80);
        ihm.put("java",90);

        System.out.println(ihm);
    	}
}
```

输出：

	{java=90, 语文=90, 语文=89}

这个程序的输出很特别，又要讨论 new string "A" 和 "A" 之间的区别了。前面两个是字符串对象而后面的是字符串直接量，前面两个字符串对象 通过"=="比较不相等，而字符串直接量通过"=="相等。

#### SortedMap 接口和 TreeMap 实现类

正如 Set 接口派生出 SortedSet 子接口 ，sortedSet 子接口有一个 TreeSet 实现类一样。Map 也有一个 SortedMap 子接口 和 一个 TreeMap 实现类。





#### EnumMap实现类



### 8. 操作集合的工具类 collections

#### 排序操作

#### 查找替换操作

#### 同步控制操作

线程安全的集合有哪些？

Collections 类中提供了多个 synchronizedXXX() 方法，可以将指定集合包装成线程同步的集合，从而可以解决多线程并发访问集合的线程安全问题。封装的办法很简单：
	
	List list = Collections.synchronizedList(new ArrayList());

然后可以像普通集合一样去操作它们了。


#### 繁琐的接口:Enumeration

Enumeration 接口用于遍历古老的类，类似于：Vector和Hashtable，而一些现代类已经不支持了。




### 9. 重点来了

通过这里，我们可以很轻松的去接触 Map,Set,List 底层的实现原理，因为这里先介绍一些底层所需要的基本概念。

* HashSet以及子类，采用 hash 决定集合中元素的存储位置，并通过 hash算法来决定集合大小
* HashMap，Hashtable而言，采用 hash 算法来决定 Map 中 key 的存储，并通过 hash 算法来增加 key集合的大小。

hash 表中存储元素的位置被称为"桶bucket",一般一个桶存储一个元素，这时候效率肯定最好啊，但是发生 hash 冲突的情况下，一个桶里面可能有多个元素，这些元素以链表的形式存在，必须按顺序搜索。

简单说，hash 表包含以下属性：

* 容量(capacity):hash表中桶的数量。
* 初始化容量(initial capacity):创建hash表时桶的容量。
* 尺寸(size):当前hash表中记录的数量。
* 负载因子(load factor):负载因子=size/capacity,负载因子为0，表示空的hash表，0.5表示半满的
* 负载极限(rehashing):当 hash 表中的负载因子达到指定的"负载极限"时，hash表会自动成倍的增加桶的数量，将原有对象重新分配，装入新的桶中，称为 rehashing。默认 0.75。


### 10. equals() 和 HashCode()

根据以下学习目录可以参考这篇博客[探索equals()和hashCode()方法](http://blog.csdn.net/speedme/article/details/22528047)


### 思考


* equals() 和 HashCode() 之间的区别

* 为什么选择 HashCode() 方法

* 为什么要重写 equals() 方法,即引出 重写equals()的原由 

* 重写了 equals()之后，总是要重写 HashCode()


### 参考

* [HashCode初始](http://www.importnew.com/18851.html)

* [HashMap源码](http://www.importnew.com/16650.html)

* [泛型加深](http://www.importnew.com/12364.html)


 



