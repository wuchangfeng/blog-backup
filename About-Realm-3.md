---
title: Realm 的学习笔记-3
toc: true
date: 2016-06-29 07:52:56
tags: realm
categories:
description:
feature:
---

这一节主要记录关于 Ream 中实例的生命周期以及 Intent 的用法的笔记。

<!--more-->



## 1. Realm 实例的生命周期

RealmObjects` 和 `RealmResults 在访问其引用数据时都是懒加载的。因为这个原因，请不要关闭你的 Realm 实例如果你仍然需要访问其中的 Realm 对象或者查询结果。为了避免不必要的 Realm 数据连接的打开和关闭，Realm 内部有一个基于[引用计数](https://en.wikipedia.org/wiki/Reference_counting)的缓存。这表示在同一线程内调用`Realm.getDefaultInstance()` 多次是基本没有开销的，并且底层资源会在所有实例都关闭的时候才被释放。

所以官方建议在 Application 中来初始化 realm 实例。如下即我的默认配置:

```java
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
```



官方文档中这样一句话也能带来一些思考：

```
// onCreate()/onDestroy() overlap when switching between activities so onCreate()
// on Activity 2 will be called before onDestroy() on Activity 1.
```

## 2. Realm 中 Intent 的使用

Realm 中竟然不支持 Intent 来传递 RealmObject。官方建议是传递一个 RelamObject 的标识符。即设置一个对象的主键。从而在 Intent 中将这个对象的主键作为传递参数。

在接受方（Activty、Service、IntentService、BroadcastReceiver 及其它）从 bundle 中解析出这个主键然后打开 Realm 查询得到这个 `RealmObject`。

如下即为调试所取得的：

```java
public class Dog extends RealmObject {
    @PrimaryKey
    private String id;
  	private String name;
  	private String age;
}
```



```java
   @OnClick(R.id.btn_add_data)
    public void addDataOnClick(){
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                dog = realm.createObject(Dog.class);
                dog.setName("dogwu");
                dog.setAge(12);
                dog.setId(UUID.randomUUID().toString());
            }
        });
    }
```

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    // Clear out all Person instances.
    realm.delete(Dog.class);
    realm.close();
}
```

```java
@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        realm = Realm.getDefaultInstance();
        if(getIntent()!=null){
            String dogId = getIntent().getStringExtra("dog_id");
            if(dogId!=null){
                Dog dog = realm.where(Dog.class)
                                .equalTo("id",dogId)
                                .findFirst();
                Log.e(TAG,dog.toString());
            }
        }
    }
```

```java
E/ReceiveActivity: Dog = [{id:f488d4e1-bf68-447f-ad66-adde388e7c08},{name:dogwu},{age:12}]
```

## 3. Relam 中的线程

你可以实时在不同线程中读取和写入 Realm 对象，不用担心其它线程会对同一对象进行操作。你需要在改变对象时使用事务，在另一线程中指向同一对象的数据会被即时更新（更新会在下一次事件循环时进行）

**不能随意跨线程传递 Realm 对象** 

如果你在另一线程使用同一对象，请在哪个线程使用查询重新获得该对象。请谨记所有的 Realm 对象都会在不同线程中保持更新——Realm 会在数据改变时[通知](https://realm.io/cn/docs/java/latest/#notifications)你。

```java
// in a Fragment or Activity, etc
@Override
public void onActivityCreated(Bundle savedInstanceState) {
    // ... boilerplate omitted for brevity
    realm = Realm.getDefaultInstance();
    // get all the customers
    RealmResults<Customer> customers = realm.where(Customer.class).findAllAsync();
    // ... build a list adapter and set it to the ListView/RecyclerView/etc

    // set up a Realm change listener
    changeListener = new RealmChangeListener<RealmResults<Customer>>() {
        @Override
        public void onChange(RealmResults<Customer> results) {
            // This is called anytime the Realm database changes on any thread.
            // Please note, change listeners only work on Looper threads.
            // For non-looper threads, you manually have to use Realm.waitForChange() instead.
            listAdapter.notifyDataSetChanged(); // Update the UI
        }
    };
    // Tell Realm to notify our listener when the customers results
    // have changed (items added, removed, updated, anything of the sort).
    customers.addChangeListener(changeListener);
}

// In a background service, in another thread
public class PollingService extends IntentService {
    @Override
    public void onHandleIntent(Intent intent) {
        Realm realm = Realm.getDefaultInstance();
        // go do some network calls/etc and get some data and stuff it into a 'json' var
        String json = customerApi.getCustomers();
        realm.beginTransaction();
        realm.createObjectFromJson(Customer.class, json); // Save a bunch of new Customer objects
        realm.commitTransaction();
        // At this point, the data in the UI thread is already up to date.
        // ...
    }
    // ...
}
```

文档中说明是一旦后台服务添加了新用户，customer 列表会被自动更新，不需要任何动作。

```java
realm.createObjectFromJson(Customer.class, json); // Save a bunch of new Customer objects
```

上面直接将 Json 数据报错为 realm 对象，并且保存。

最后，我们在想要结束监听的地方作如下操作:

```java
realm.removeAllChangeListeners();
```

