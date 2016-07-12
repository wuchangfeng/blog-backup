---
title: Comparable 和 Comparator 的比较
date: 2015-08-31 10:24:00
tags: java
categories: About Java
---
Java中Comparable和Comparator区别

<!-- more -->

## Comparable 

Comparable排序接口 位于 java.lang 下。

若某个类实现了 Comparable排序接口，则表明该类支持排序。

一般的集合只要实现了 Comparable 最后都会 collections.sort() 从而自然排序。

底下这个按照年龄进行排序，User 类实现了 Compareable 接口，并复写了 CompareTo() 方法。为了方便 我讲 user 这个类直接抽离出来，实际上 CompareTo() 完全可以单独存在。

``` java
	class User implements Comparable {

    private String id;
    private int age;

    public User(String id, int age) {
        this.id = id;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int compareTo(Object o) {
        System.out.println("=");
        return this.age - ((User) o).getAge();
    	}
	}

	public class StringComparabelTest1 {

    	public static void main(String[] args) {
    	    User[] users = new User[] { new User("a", 30), new User("b", 20) };
    	    Arrays.sort(users);
    	    for (int i = 0; i < users.length; i++) {
    	        User user = users[i];
    	        System.out.println(user.getId() + " " + user.getAge());
    	    }
    	}
	}
```

再看一个本身就实现了 Compareable 接口的集合，即 CompareTo() 方法已经写好

``` java
	public class StringComparableTest {

    public static void main(String[] args) {
        //String类已实现Comparable接口
        //public final class String implements java.io.Serializable,
        // Comparable<String>, CharSequence(){}
        List<String> stringList = new ArrayList<>();
        stringList.add("a");
        stringList.add("c");
        stringList.add("b");
        stringList.add("i");
        stringList.add("v");
        System.out.println("排序前："+stringList);

        // 使用Collections中的sort(List<T> list)方法实现排序功能
        // public static <T extends Comparable<? super T>> void sort(List<T> list){}
        /*
         * extends ：在泛型中表示继承或实现
         * T extends Comparable：说明T类型必须实现Comparable接口
         *  <? super T>  ：说明List中泛型的类型必须为T类型或其子类型
         * */
        Collections.sort(stringList);
        System.out.println("排序后："+stringList);
    	}
	}
```


## Comparator 

Comparator 是比较器接口。

我们若需要控制某个类的次序，而该类本身不支持排序(即没有实现Comparable接口)；那么，我们可以建立一个“该类的比较器”来进行排序。这个“比较器”只需要实现Comparator接口即可。
也就是说，我们可以通过“实现Comparator类来新建一个比较器”，然后通过该比较器对类进行排序

``` java
	package java.util;
	public interface Comparator<T> {
    	int compare(T o1, T o2);
    	boolean equals(Object obj);
	}
```

* 每个实现 比较器的类必须要实现 compare() 函数，但是 equals() 可以不用，因为所有类都已经实现了 equals()
* 比较器可以实现多种排序方法，升序，降序等等。


## 参考

[参考一](http://jokerlinisty.iteye.com/blog/2188676)
[参考二](http://www.iteye.com/problems/3025)