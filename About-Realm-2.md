---
title: Realm 的学习笔记-2
toc: true
date: 2016-06-29 07:52:42
tags: realm
categories:
description:
feature:
---

这一节主要记录关于 Ream 中表之间的各种关系的笔记。

<!--more-->

多对多的关系

通过使用 RealmList<T> 为一个对象关联0个或者多个其他对象。

```java
public class Person extends RealmObject {
    public String id;
  	public String name;
    public RealmList<Dog> dogs;
}
public class Dog extends RealmObject {
    public String id;
    public String name;
  	public String color
}
```

RealmList 是 Realm 模型对象的容器。

其行为与 Java 的普通 `List` 近乎一样。同一个 Realm 模型对象可以存在于多个 `RealmList` 中。同一个 Realm 模型对象可以在同一个 `RealmList` 中存在多次。你可以使用 `RealmList` 来表现一对多和多对多的数据关系。

添加的行为就像下面这样:

```java
realm.executeTransaction(new Realm.Transaction() {
    @Override
    public void execute(Realm realm) {
         Person user2;
                Person user1;
                user1 = realm.createObject(Person.class);
                user1.name = "Allen wu";
                user2 = realm.createObject(Person.class);
                user2.name = "Allen zhang";

                Cat cat1 = realm.createObject(Cat.class);
                cat1.name = "cat1";
                cat1.color = "red";
                user1.cats.add(cat1);

                Cat cat2 = realm.createObject(Cat.class);
                cat2.name = "cat2";
                cat2.color = "yellow";
                // dogs 是 Person 的属性
                user1.cats.add(cat2);
                user2.cats.add(cat2);

                Cat cat3 = realm.createObject(Cat.class);
                cat3.name = "cat3";
                cat3.color = "red";
                user2.cats.add(cat3);

                Cat cat4 = realm.createObject(Cat.class);
                cat4.name = "cat4";
                cat4.color = "green";
                // dogs 是 Person 的属性
                user2.cats.add(cat4);
                Log.i(TAG,"Insert success");
    }
});
```

这样,我们就添加了两个 Dog 对象到 Person 中。

进而引申到了关联查询啦，这个图也是来自 Realm 的官方文档。

![LinkQuery.png](http://7xrl8j.com1.z0.glb.clouddn.com/LinkQuery.png)

查询语句如下:

```java
@OnClick(R.id.btn_query)
    public void queryOnclick(){
        // 所有至少含有一个 color 为 red 的 person
        RealmResults<Person> persons = realm.where(Person.class)
                                            .equalTo("cats.color","red")
                                            .findAll();
        // 可以遍历 person
        Log.e(TAG,"关联查询1"+persons.get(0).getCats());
        Log.e(TAG,"关联查询2"+persons.get(1).getCats());
    }
```

输出如下结果:

```java
06-24 11:15:31.990 28171-28171/com.allen.myrelamdemo E/MyRelamDemo: 关联查询1Cat@[0,1]
06-24 11:15:31.990 28171-28171/com.allen.myrelamdemo E/MyRelamDemo: 关联查询2Cat@[1,2,3]
```

具体更加复杂的关联查询可以看 [关联查询](https://realm.io/cn/docs/java/latest/#section-9)

另外 在开发和调试过程中，假如你需要频繁改变数据模型，*并且不介意损失旧数据*，你可以直接删除 `.realm` 文件（这里包含所有的数据！）而不用关心迁移的问题。这在你应用的开发早期阶段非常有用。

```java
RealmConfiguration config = new RealmConfiguration.Builder(context)
    .deleteRealmIfMigrationNeeded()
    .build()
```

如果是后期的话，可以详细看看[数据迁移相关](https://realm.io/cn/docs/java/latest/#migrations)