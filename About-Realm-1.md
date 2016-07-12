---
title: Realm 的学习笔记-1
toc: true
date: 2016-06-29 07:52:20
tags: realm
categories:
description: 
feature:
---

这一节主要记录关于 Ream 的初步配置以及基本用法的笔记。

<!-- more -->

## 1. 在 AS 中配置 Relam

![Relam1.png](http://7xrl8j.com1.z0.glb.clouddn.com/Relam1.png)

在 1 文件中顶头加入下面这行代码:

```java
apply plugin: 'realm-android'
```

在 2 文件中做如下配置:

```java
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.1.0'
        classpath "io.realm:realm-gradle-plugin:1.0.1"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

## 2.  初步使用

在 onCreate() 中作如下写入:

```java
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取一个 realm 实例
        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);
    }
```

在 onDestory() 中作如下写入:

```java
 @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
```



Model 的构建只需要继承 RealmObject 即可。其他跟构建普通 Model 没啥区别。只不过继承 RealmObject 之后,可以使用其提供的多种注解。 

写入操作,省去了 Model 的构建，并且直接基于 事务执行块(Transaction blocks)

```java
realm.executeTransaction(new Realm.Transaction() {
	@Override
	public void execute(Realm realm) {
		User user = realm.createObject(User.class);
		user.setName("John");
		user.setEmail("john@corporation.com");
	}
});
```

基本的查询操作如下,有关查询的一些条件可以参见 [查询条件](https://realm.io/cn/docs/java/latest/#section-15)

```java
RealmResults<User> r = realm.where(User.class)
                            .greaterThan("age", 10)  //implicit AND
                            .beginGroup()
                                .equalTo("name", "Peter")
                                .or()
                                .contains("name", "Jo")
                            .endGroup()
                            .findAll();
```

基本的删除操作如下:

```java
// obtain the results of a query
final RealmResults<Dog> results = realm.where(Dog.class).findAll();
// All changes to data must happen in a transaction
realm.executeTransaction(new Realm.Transaction() {
    @Override
    public void execute(Realm realm) {
        // remove single match
        results.deleteFirstFromRealm();
        results.deleteLastFromRealm();
        // remove a single object
        Dog dog = results.get(5);
        dog.deleteFromRealm();
        // Delete all matches
        results.deleteAllFromRealm();
    }
});
```

查询结果的自动更新,这个特性简直好到爆。来看一个 Demo:

在 Oncreate() 中我们做如下操作:

```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        final RealmResults<Dog> puppies = realm.where(Dog.class)
                .lessThan("age", 2)
                .findAll();
        Log.e(TAG,"before update:"+puppies.size());

        puppies.addChangeListener(new RealmChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> results) {
                Log.e(TAG,"after update:"+ puppies.size());
                Log.e(TAG,"after update:"+ results.size());
            }
        });
    }
```

设置如下事件：

```java
 @OnClick(R.id.btn_show)
    public void showOnClick(){
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                Dog dog = realm.createObject(Dog.class);
                dog.setAge(1);
                dog.setName("Allen");
            }
        });
    }
```

当你点击 Button 时候，改变了一些属性的值，查询结果竟然自动更新。也就是讲 onCreate() 中自动打印出更新后查询结果。

最后一点就是迭代啦:

很多情况下,都是返回一个 RealmResults :

```java
RealmResults<User> results = realm.where(User.class).findAll();
for (User u : results) {
    // ... do something with the object ...
}
```

当然 for 循环也是可以的。

模型自己去定义啦，但是需要继承 RealmObject

## 3. Relam 的最佳配置

通过 realm.getInstance() 来访问我们已经初始化的 realm 变量。可以通过 realm.getPath() 来获取绝对路径，比如我的：

```java
/data/data/com.allen.myrelamdemo/files/default.realm
```

配置 Realm

默认的当然:

```java
RealmConfiguration config = new RealmConfiguration.Builder(context).build();
```

典型的配置如下:

```java
RealmConfiguration config = new RealmConfiguration.Builder(context)
  .name("myrealm.realm")
  .encryptionKey(getKey())
  .schemaVersion(42)
  .modules(new MySchemaModule())
  .migration(new MyMigration())
  .build();
// Use the config
Realm realm = Realm.getInstance(config);
```

当然我最喜欢的还是在 Application 中来配置啦，保存为默认配置，这样全局就可以用啦：

```java
public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // The Realm file will be located in Context.getFilesDir() with name "default.realm"
    RealmConfiguration config = new RealmConfiguration.Builder(this).build();
    Realm.setDefaultConfiguration(config);
  }
}
```

在任意 Activity 或者 Fragment 中:

```java
Realm realm = Realm.getDefaultInstance();
```



##  4. Relam 中的模型字段



@Required 修饰的字段,表示告诉 Realm 强制禁止空值（null）被存储。

@Ignore  意味着一个字段不应该被保存到 Realm。

@PrimaryKey 可以用来定义字段为主键，主键的存在意味着可以使用 `copyToRealmOrUpdate()` 方法，它会用此主键尝试寻找一个已存在的对象，如果对象存在，就更新该对象；反之，它会创建一个新的对象。当 `copyToRealmOrUpdate()` 的调用对象没有主键时，会抛出异常

### Auto-Updating Objects

RealmObject 是实时的、自动更新的底层数据的映射视图。你不需要去重新获得**对象**已取得其最新版本。对于数据的改动会即时反应到**相关的对象或者查询结果**。

```java
final Dog myDog;
realm.executeTransaction(new Realm.Transaction() {
    @Override
    public void execute(Realm realm) {
        myDog = realm.createObject(Dog.class);
        myDog.setName("Fido");
        myDog.setAge(1);
    }
});

realm.executeTransaction(new Realm.Transaction() {
    @Override
    public void execute(Realm realm) {
        Dog myPuppy = realm.where(Dog.class)
          				   .equalTo("age", 1)
          				   .findFirst();
        myPuppy.setAge(2);
    }
});

myDog.getAge(); // => 2
```



### Customizing Objects(定制对象)

之前这一点我也不明白，看了文档明白多了，如下所示我们只定义了属性，并没有写方法。

```java
public class Dog extends RealmObject {
    public String name;
    public int age;
}
```



```java
@OnClick(R.id.btn_customizing)
    public void customizingOnClick(){
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                Bird bird = realm.createObject(Bird.class);
                bird.name = "xiaoniao";
                bird.color = "red";
                Log.e(TAG,bird.color);// =>red
            }
        });
    }
```

我们可以直接访问其属性，而模型并没有 get/set 方法。

## 

