---
title: Java-LinkedList
date: 2016-04-16 18:48:48
tags: javacode
categories: About Java
---
java 源码分析-LinkedList

<!--more-->



## 初识 1. LinkedList

其内部以链表形式保存集合中的元素，因此随机访问集合元素性能较差，但是在插入，删除元素时性能较好。下图为链表的数据结构示意图：

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E5%8D%95%E9%A1%B9%E9%93%BE%E8%A1%A8.jpg)

而 **LinkedList 采用的为双向链表结构**，如下图所示：

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E5%8F%8C%E5%90%91%E5%BE%AA%E7%8E%AF%E9%93%BE%E8%A1%A8.jpg)


**声明**：[图片来自该简书博客](http://www.jianshu.com/users/a7f72d78fe0d/latest_articles)

其实现了 List 接口(索引访问)以及 Deque 接口(双端队列，栈和队列)，用法如下：

```java
	public class LinkedListTest {

    public static void main(String[] args) {
        LinkedList books = new LinkedList();
        //将字符串元素加入到队列的尾部
        books.offer("book1");
        //加入栈的顶部
        books.push("book2");
        //添加到队列的头部，相当于栈的顶部
        books.offerFirst("book3");
        //按索引的方式来访问，即 List
        for (int i = 0; i < books.size(); i++) {
            System.out.println("遍历中：" + books.get(i));
        }
        //访问并不删除栈顶的元素
        System.out.println(books.peekFirst());
        //访问并不删除队列的最后一个元素
        System.out.println(books.peekLast());
        //栈顶元素弹出“栈”
        System.out.println(books.pop());
        //队列中的第一个元素将被删除
        System.out.println(books);
        //访问并删除队列的最后一个元素
        System.out.println(books.pollLast());
        System.out.println(books);
    }
	}
```
输出：
	
	遍历中：book3
	遍历中：book2
	遍历中：book1
	book3
	book1
	book3
	[book2, book1]
	book1
	[book2]



## 2. LinkedList 源码分析


1 LinkedList 定义

```java
	public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```

2 LinkedList 属性定义

``` java
	transient int size = 0;
    //永远指向第一个节点
    transient Node<E> first;
    //永远指向最后一个节点
    transient Node<E> last;
```

3 LinkedList 构造函数定义

``` java
	 public LinkedList() { }

    // 构造一个包含指定集合 C 的列表
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
```

4 Node<> 节点的定义(在 c/c++ 中相当于结构体了)

```java
	 private static class Node<E> {
        E item;     //当前节点的 数值
        Node<E> next;// 下一个节点
        Node<E> prev; // 前一个节点

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

5 原子操作函数,私有方法，相当于给本类用的，外部不能调用

``` java
	//头插入元素
	private void linkFirst(E e) {    
		//构建一个prev值为null,节点值为e,next值为f的新节点newNode
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
		//newNode 赋值给 first
        first = newNode;
		//如果 next 为 null，则 last 也为 newNode，即只有一个节点
        if (f == null)
            last = newNode;
		//不然的话 nextNode 的前继节点为 newNode
        else
            f.prev = newNode;
        size++;
        modCount++;
    }
    //尾插入元素
    void linkLast(E e) {
		//获取最后一个节点
        final Node<E> l = last;
		//newNode 其前节点为 l，后节点为 null
        final Node<E> newNode = new Node<>(l, e, null);
		//新节点赋值给最后一个
        last = newNode;
		//如果之前的最后一个节点为空，说明为空list，则插入的 newNode 为 firstNode
        if (l == null)
            first = newNode;
		//l 不为空，则l.next 为 newNode
        else
            l.next = newNode;
        size++;
        modCount++;
    }
    //指定位置插入节点
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }
    //删除链表第一个元素
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
    // 删除尾节点，并返回该节点的元素
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;  // 尾节点上面的数值置null
        l.prev = null; // help GC
        last = prev;   // 最后一个节点设为尾节点的前驱
        if (prev == null) //如果原来的节点前驱为null，表示只有一个节点，再删除的话，链表就是null了
            first = null;
        else
            prev.next = null;// 否则 原节点的 前驱节点的 next 节点就为 null 了
        size--;      // 列表长度-1
        modCount++;
        return element;// 返回删除的元素
    }
    //删除指定节点元素
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        modCount++;
        return element;
    }
```

6 LinkedList操作

``` java
	//获取第一个元素
	public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }
    //删除第一个
	public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);//原子操作
    }
    //移除首次出现在 linkedlist 中的元素，允许重复，且为null
	public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {//顺序访问，直到null
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    //从指定的位置，插入集合中的元素
	public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);//合法性

        Object[] a = c.toArray(); //集合转换成数组
        int numNew = a.length;
        if (numNew == 0)
            return false;

        Node<E> pred, succ;
        if (index == size) {  // 这不就是在直接尾部插入吗
            succ = null;      // 插入元素的后续节点为 null
            pred = last;	  // 插入元素的前驱 为 last
        } else {
            succ = node(index);//插入节点的 后续节点为 要插入的位置
            pred = succ.prev; // 插入节点的 前驱为插入节点的 prve
        }

        for (Object o : a) {  // 循环插入数组中的元素
            @SuppressWarnings("unchecked") E e = (E) o;//集合不使用泛型，抑制了该警告
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

	//根据索引获取元素
	public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }
    //返回指定位置处的节点,使用了折半查找的思想
	Node<E> node(int index) {
     
        if (index < (size >> 1)) { //这里又来了位操作，右移位一位为原来的1/2
            Node<E> x = first;
            for (int i = 0; i < index; i++)//因为是链表，不能随机访问，而随机访问的内部还是通过顺序访问来实现的。循环直到 i=index 结束
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }
```

7 Queue操作

Queue操作提供了peek()、element()、poll()、remove()、offer(E e)这些方法。

``` java
    public E peek() { //获取队列头部元素，但是不删除，若队列为空，返回null
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }
    // 返回队列第一个元素
    public E element() {
        return getFirst();
    }
    //返回队列头部元素，并删除。若为空，返回null
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    //返回队列头部元素，并删除
    public E remove() {
        return removeFirst();
    }
    //指定元素加入队列尾部
    public boolean offer(E e) {
        return add(e);
    }
```

8 Deque（双端队列）操作

Deque操作提供了offerFirst(E e)、offerLast(E e)、peekFirst()、peekLast()、pollFirst()、pollLast()、push(E e)、pop()、removeFirstOccurrence(Object o)、removeLastOccurrence(Object o)这些方法。

``` java
	//双端队列操作
    public boolean offerFirst(E e) {	
        addFirst(e);
        return true;
    }
    //插到尾部
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }
    //获取但不删除第一个元素
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
     }
    //获取但不删除最后一个元素
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }
	//获取并删除 first
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    //获取并删除 last
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }
```


9 其他方法

9.1 克隆(浅克隆)

``` java
	   public Object clone() {
        LinkedList<E> clone = superClone();

        //克隆过后的一些参数初始化
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;
        //把原链表的元素赋值到克隆后的链表中
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }
```

9.2 toArray()

``` java
	public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }
```

9.3 <T> T[] toArray(T[] a)

``` java
	public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }
```
如果给定的参数数组长度足够，则将ArrayList中所有元素按序存放于参数数组中，并返回
如果给定的参数数组长度小于LinkedList的长度，则返回一个新分配的、长度等于LinkedList长度的、包含LinkedList中所有元素的新数组。

这里利用了反射来动态创建数组，根据原先的数组类型以及大小。具体创建可参见 疯狂 java P841

9.4 writeObject()

``` java
	private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }
```

自定义序列化写入方式。

10 Fail-Fast机制

LinkedList也采用了快速失败的机制，通过记录modCount参数来实现。在面对**并发的修改**时，迭代器很快就会完全失败，而不是冒着在将来某个不确定时间发生任意不确定行为的风险。



## 3. ArrayList 与 LinkedList 之间的区别

* ArrayList是实现了基于**动态数组的数据结构**，LinkedList基于**链表**的数据结构。
* LinkedList不支持**高效的随机元素**访问。
* ArrayList的空间浪费主要体现在在list列表的结尾预留一定的容量空间，而LinkedList的空间花费则体现在它的每一个元素都需要消耗相当的空间，就存储密度来说，ArrayList是优于LinkedList的。
* LinkedList既然是通过双向链表去实现的，那么它可以被当作 堆栈、队列或双端队列 进行操作
* ArrayList 实现了 RandomAccess接口，而 LinkedList 却没有



## 4. 总结

* 其实内部也还是挺简单的，定了一个 Node 节点，就像 结构体一样。
注意：由于 LinkedList 以链表来维护元素的顺序结构(插入的顺序)，导致其随机访问的性能不是太好(特别注意，其支持随机访问 get()),但是其内部还是通过顺序访问来实现的，一个一个的走，直到匹配了索引。

* LinkedList元素可以为 null。

* 为此，java 开发人员，在内部还是用了** 位操作**，变相的就是提供了**二分查找**来稍微那么提高一点效率。

* 关于位操作，简单记忆一下就是 size >> n,其值就变为原来的 2的n次方分之一，同理向左移就是 2的n次方倍了哦。



## 5. 参考

[LinkedList 源码分析](http://www.jianshu.com/p/681802a00cdf)

[LinkedList源码分析](http://www.tuicool.com/articles/NbQFbm)