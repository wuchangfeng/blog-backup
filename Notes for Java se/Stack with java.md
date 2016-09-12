---
title: Java 中 stack 的自定义实现
date: 2015-09-06 11:16:49
tags: java
categories: About Java
---
java 数据结构之栈的自定义实现以及JDK中的实现

<!--more-->

### 栈的自定义实现(采用数组)

``` java
class MyStack{

    private int[] array;
    private int top;//相当于栈顶指针

    public MyStack(){

        array = new int[10];
        top = -1;//栈顶指针为 -1 表示为空栈
    }

    public MyStack(int size){

        array = new int[size];
        top = -1;
    }

    public boolean isEmpty(){

        boolean flag = top == -1;

        return flag;
    }

    public void push(int e){

        if(top == array.length-1)
            throw new StackOverflowError();
        else
            array[++top] = e;

    }

    public int pop(){

        if (top == -1)
            throw new EmptyStackException();
        else
            return array[--top];
    }

    /**
     * just look the first e and will not pop it
     * @return
     */
    public int peek(){

        if (top == -1)
            throw new EmptyStackException();
        else
            return array[top];

    }
}

public class StackRealizeTest {

    public static void main(String[] args) {

        //JDK 栈
        Stack stack = new Stack();
        //自定义栈
        MyStack myStack = new MyStack(10);

        System.out.println(myStack.isEmpty());
        //压栈初始化
        for (int i = 0; i < 10; i++) {

            myStack.push(i);
        }
        //查看栈是否为空
        System.out.println(myStack.isEmpty());
        //查看栈顶元素
        System.out.println(myStack.peek());
        //弹栈
        System.out.println(myStack.pop());
        System.out.println(myStack.pop());
    }
}
```

### JDK 中栈的实现

``` java
public class Stack<E> extends Vector<E> {
    /**
     *构造函数
     */
    public Stack() {
    }
	
	/**
     *入栈
     */
    public E push(E item) {
        addElement(item);

        return item;
    }

   	/**
     *出栈
     */
    public synchronized E pop() {
        E       obj;
        int     len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     */
    public synchronized E peek() {
        int     len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

    /**
     * Tests if this stack is empty.
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * 查找
     */
    public synchronized int search(Object o) {
		//交给 vector
        int i = lastIndexOf(o);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1224463164541339165L;
}
```

看源码还是比较简单的，但是有一些需要注意，stack 是继承 vector 的，许多方法 stack 都没有去操心，都让 vector 去解决了，比如 入栈的操作，**addElement** 方法将后面的细节都封装了起来。如果我们更加深入的去考虑这个问题的话，我们会发现几个需要考虑的点。1. 首先，数组不会是无穷大的 ，所以不可能无限制的让你添加元素下去。当我们数组长度到达一个最大值的时候，我们不能再添加了，就需要抛出异常来。2. 如果当前的数组已经满了，实际上需要扩展数组的长度。常见的手法就是新建一个当前数组长度两倍的数组，再将当前数组的元素给拷贝过去。前面讨论的这两点，都让vector把这份心给操了。

[参考](http://shmilyaw-hotmail-com.iteye.com/blog/1825171)