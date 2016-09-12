---
title: Arts-Development-of-Android-11-3
toc: true
date: 2016-08-17 21:56:39
categories: 读书笔记
tags: Android
description:
feature:
---

本篇介绍 HandlerThread 和 IntentService 相关概念。

<!--more-->

### 引言

* HandlerThread 是什么？能做什么？与 Handler 和 Thread 是何种关系？
* IntentService 是什么？与 Service 是何种关系？是线程吗？与线程何种关系？有什么优点？

## 一. HandlerThread

从 handlerThread 的实现来看它和普通的 Thread 有着显著的不同，普通的 Thread 中的 run() 方法都是在执行一个耗时任务，而 HandlerThread 的 run() 方法中却创建了消息队列，外界需要通过 handler 的消息方式来通知 HandlerThread 来执行一个具体的任务。如果你想看一个实例以及源码解析可以看[彻底了解 HandlerThread](http://blog.csdn.net/lmj623565791/article/details/47079737)

``` java
public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
}
```



## 二. IntentService

之前我们有情况需要在 Service 进行一些后台的耗时操作，但是又不能直接在 Service 中进行，此时会选择在 Service 中再开一个线程，但是这样不够优雅，因为我们需要自己去管理 Service 的生命周期以及子线程。

IntentService 是一种特殊的服务(封装了 HandlerThread 和 Handler)，继承与 Service 并且是一个抽象类。并且由于它是服务的原因导致他的优先级比单纯的线程高很多。IntentService 适用于高优先级的 Service(不容易被杀死)。IntentService 封装了 Handler 和 HandlerThread。你可以通过 startService(Intent) 来提交请求，该 Service会在需要的时候创建，当完成所有的任务以后自己关闭，且请求是在工作线程处理的。

按照鸿洋博客上面的例子，写出 Demo 可以看文章结束处链接(实际 Demo 不要忘了注册服务)，主要效果就是模拟耗时的上传操作，并且用广播在 Service 与 Activity 之间通信。

``` java
public class UploadImgService extends IntentService
{
    private static final String ACTION_UPLOAD_IMG = "com.zhy.blogcodes.intentservice.action.UPLOAD_IMAGE";
  
    // 复写 onHandleIntent 根据传入的 intent 来选择具体的操作
    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_IMG.equals(action))
            {
                final String path = intent.getStringExtra(EXTRA_IMG_PATH);
              	// 耗时操作
                handleUploadImg(path);
            }
        }
    }
  
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("TAG","onCreate");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("TAG","onDestroy");
    }
}
```

我们对上面代码进行分析：onHandleIntent 方法会从参数中解析出具体的后台任务标识,然后根据不同的标识来执行具体的任务。而我们通过实验可知，发起多个任务之后，任务在后台是排队执行的，并且在最后一个任务执行完毕之后，IntentService 才真正的停止。

分析 IntentService 的源码如下：

``` java
public abstract class IntentService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private boolean mRedelivery;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
          	// 回调 onHandleIntent()
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

    public IntentService(String name) {
        super();
        mName = name;
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
                super.onCreate();
      	// 初始化 HandlerThread
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
      	// Handler 发送消息
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      	// 回调 onStart()
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

	// 抽象方法
    protected abstract void onHandleIntent(Intent intent);
}
```

注意下：回调完成后回调用 stopSelf(msg.arg1)，注意这个 msg.arg1 是个 int 值，相当于一个请求的唯一标识。每发送一个请求，会生成一个唯一的标识，然后将请求放入队列，当全部执行完成(最后一个请求也就相当于getLastStartId == startId)，或者当前发送的标识是最近发出的那一个（getLastStartId == startId），则会销毁我们的Service.很简单的道理如果我们使用 stopSelf() 的话，会立即停止服务，这时候可能还会有其他的消息没有处理，如果传入的是 -1 则直接销毁。

那么，当任务完成销毁 Service 回调 onDestory，可以看到在 onDestroy 中释放了我们的Looper:mServiceLooper.quit()。

### 三. 参考和扩展阅读

* 《Android 开发艺术探索》
* [当 Service 遇上 Handler](http://blog.csdn.net/lmj623565791/article/details/47143563)