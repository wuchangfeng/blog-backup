---
title: 设计模式之迭代器模式
date: 2016-05-01 20:35:59
tags: design-pattern
categories: About Java
---


迭代器模式提供一种方法，顺序访问一个集合对象中的各个元素，但是又不暴露其内部元素的表示。这样让集合的接口和实现变得简单，也可以让集合更加专注于管理集合中的元素，而不用花费精力去理会遍历上的事情。迭代器模式允许访问集合中的元素，而不暴露其内部的结构，即其内部到底存储方式是**数组**还是**List**。迭代器模式将遍历集合的工作封装进一个对象中。迭代器模式提供了一个通用的接口。

### 角色定义

* 抽象容器：一般是一个接口，提供一个iterator()方法，例如java中的Collection接口，List接口，Set接口等。

* 具体容器：就是抽象容器的具体实现类，比如List接口的有序列表实现ArrayList，List接口的链表实现LinkList，Set接口的哈希列表的实现HashSet等。

* 抽象迭代器：定义遍历元素所需要的方法，一般来说会有这么三个方法：取得第一个元素的方法first()，取得下一个元素的方法next()，判断是否遍历结束的方法isDone()（或者叫hasNext()），移出当前对象的方法remove(),

* 迭代器实现：实现迭代器接口中定义的方法，完成集合的迭代。

### 实例讲解

抽象迭代器接口：
``` java
interface Iterator {
	// 取得下一个元素
    public Object next();
	// 判断是否有下一个元素存在
    public boolean hasNext();
}
```

具体迭代器：
``` java
class ConcreteIterator implements Iterator{
    private List list = new ArrayList();
    private int cursor =0;
    public ConcreteIterator(List list){
        this.list = list;
    }
    
	public boolean hasNext() {
        if(cursor==list.size()){
            return false;
        }
        return true;
    }
    
	public Object next() {
        Object obj = null;
        if(this.hasNext()){
            obj = this.list.get(cursor++);
        }
        return obj;
    }
}
```

抽象容器：
``` java
interface Aggregate {
	// 添加元素
    public void add(Object obj);
	// 移除元素
    public void remove(Object obj);
	// 遍历方法
    public Iterator iterator();
}
```

具体容器，可以对比想象成Collection、List和Set等容器，这几类容器内部各不相同：
``` java
class ConcreteAggregate implements Aggregate {
    private List list = new ArrayList();
    public void add(Object obj) {
        list.add(obj);
    }

    public Iterator iterator() {
        return new ConcreteIterator(list);
    }

    public void remove(Object obj) {
        list.remove(obj);
    }
}
```

客户端测试类：
``` java
 public class Client {
    public static void main(String[] args){
		// 获取抽象容器，并且往里添加元素
        Aggregate ag = new ConcreteAggregate();
        ag.add("小明");
        ag.add("小红");
        ag.add("小刚");
		// 获取迭代器
        Iterator it = ag.iterator();
        while(it.hasNext()){
            String str = (String)it.next();
            System.out.println(str);
        }
    }
}
```

### 迭代器模式优缺点

* 简化了遍历模式，不用知道底层数据结构的实现
* 封装性良好，不用关心遍历算法是什么样的
* 可以提供多种遍历方式
* 集合需要提供迭代器