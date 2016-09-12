---
title: Arts-Development-of-Android-10
toc: true
categories: 读书笔记
tags: Android
description:
feature:
---

本篇介绍 Handler 和 Message 以及 Looper 的基本用法和工作原理。

<!--more-->

## 零. 引言

Handler 基本的用法和实例 Demo 可以看[这里](https://github.com/wuchangfeng/Blog-Resource/blob/master/Arts-Development-of-Android-10-Demo.md)。

Handler到底是什么呢？简单点将 Handler 是 Android 中引入的一种让开发者参与处理线程中消息循环的机制。每 个 Hanlder 都关联了一个线程，每个线程内部都维护了一个消息队列 MessageQueue，这样 Handler 实际上也就关联了一个消息队列。可以通过 Handler 将 Message 和 Runnable 对象发送到该 Handler 所关联线程的MessageQueue 中，然后该消息队列一直在循环拿出一个 Message，对其进行处理，处理完之后拿出下一个Message，继续进行处理，周而复始。当创建一个 Handler 的时候，该 Handler 就绑定了 **当前创建 Hanlder**的线程。从这时起，该 Hanlder 就可以发送 Message 和 Runnable 对象到该 Handler 对应的消息队列中，当从MessageQueue 取出某个 Message 时，会让 Handler 对其进行处理。

## 一. 简介

Android 中消息机制主要为 Handler 的消息机制，其底层需要 MessageQueue(消息队列用单链表来实现) 和 Looper 的支持。MessageQueue 只负责存储消息，Looper 可以来处理消息。Looper 中的 ThreadLocal 可以在每个线程中存储数据。Handler 创建时候会采用当前线程的 Looper 来构造消息循环系统，这时候就是 ThreadLocal 来获取当前线程的 Looper 了。Handler 主要作用就是将一个任务切换到某个指定的线程中去执行。Handler 创建时候,会采用当前线程的 Looper 来构建消息循环系统。

## 二. ThreadLocal 的工作原理

ThreadLocal 是一个 **线程内部的数据存储类**，通过它可以在指定的线程中存储数据，数据存储之后，只有在指定线程中才可以获取的存储的数据，对于其他线程则无法获取到数据。

对于 Handler 来讲它需要获取 **当前线程的 looper** ,这时候 ThreadLocal 就是不二之选。

从 ThreadLocal 的 set 和 get 方法可以看出 ，他们所操作的对象都是当前线程的 LocalValues 对象的 table 数组，因此在不同线程中访问同一个 ThreadLocal 的 set 和 get 方法，他们对 ThreadLoca 所做的操作仅限于各自线程的内部。

对于 ThreadLocal 的一些详细操作和解释可以参见 《Android 开发艺术探索》 P379.

## 三. MessageQueue 的工作原理

消息队列在 Android 指的是 MessageQueue . MQ 中包含两个操作：插入和读取。读取操作本身会伴随着删除操作。enqueueMessage 对应着插入方法即往消息队列中插入一条消息。next 作用是从消息队列中取出一条消息并且将其从消息队列中移除。MQ 虽然称为消息队列但是其内部实现还是**单链表**,对插入和删除操作友好嘛。next 是一个无限循环的方法，如果消息队列中没有消息则其一直阻塞。

## 四. Looper 的工作原理

Looper 在消息循环机制中扮演着循环的角色。一直不停的查看 MQ 是否有消息。有消息就立刻处理。没有就一直阻塞。**Looper 的构造方法会创建一个 MQ,**然后将当前的线程对象保存起来：

```java
private Looper(boolean quitAllowed){
  mQueue = new MessageQueue(quitAllowed);
  mThread = Thread.currentThread();
}
```

为线程创建 looper：

```java
new Thread("Thread2")
  public void run(){
  	Looper.prepare();// 为当前线程创建一个 Looper
  	Handler h = new Handler();
  	Looper.loop();// 开启
}}.start();
```

Looper 除了 prepare() 之外还提供了 prepareMainLooper 方法。其主要是主线程 ActivityThread 创建 Looper 使用的(因此在主线程中我们不用显示的去写 prepare() 方法)。本质也是通过 prepare() 方法来实现的。

而如果我们没有给 Looper 设置 prepare() 就会抛出异常(在子线程中)。为什么呢？我们就要问一下 prepare() 里面到底做了什么？看一下源码啦：

```java
public static final void prepare() {  
    if (sThreadLocal.get() != null) {  
        throw new RuntimeException("Only one Looper may be created per thread");  
    }  
    sThreadLocal.set(new Looper());  
} 
```

原来是给**每一个线程至多设置一个 Looper 对象啊！而且是通过前面的 ThreadLocal 来设置的！**

Looper 最重要的一个方法是 loop 方法，只有调用了 Loop 后，消息循环系统才会正真的起作用。

```java
public static final void loop() {  
    Looper me = myLooper();  
    MessageQueue queue = me.mQueue;  
    while (true) {  
        Message msg = queue.next(); // might block  
        if (msg != null) {  
            if (msg.target == null) {  
                return;  
            }  
            if (me.mLogging!= null) me.mLogging.println(  
                    ">>>>> Dispatching to " + msg.target + " "  
                    + msg.callback + ": " + msg.what  
                    );  
            msg.target.dispatchMessage(msg);  
            if (me.mLogging!= null) me.mLogging.println(  
                    "<<<<< Finished to    " + msg.target + " "  
                    + msg.callback);  
            msg.recycle();  
        }  
    }  
}  
```

如上即为 loop() 方法。loop() 方法的核心就是调用 MQ 的 next 方法，而 next 是一个阻塞操作，如果没有消息就一直阻塞在那里。一旦返回消息 msg.target.dispatchMessage(msg) 就会处理这条消息。**其中 msg.target** 就是发送这条消息的 Handler 对象。  

## 五. Handler 的工作原理

问题：**Handler 为什么会造成内存泄漏？** 简单点将就是 message 持有 Handler 的引用。而 Handler 又**潜在**的持有外部类的引用。[详细解释以及解决办法参见这里](http://www.jianshu.com/p/cb9b4b71a820 )

在前面的实例中我们通过 handler 将 msg 发送出去。我们自然想知道 handler 将 msg 发送到哪里去了？为什么后续还可以在 handler 中去处理 msg 呢？

通过查看源码，我们发现 sendMessage() 方法最终会通过一个叫 sendMessageAtTime() 的方法将 msg 发送出去。发送的目的地又是哪里呢？进一步查看源码是 MessageQueue 中。其中插队和出队的操作分别交给了 enqueueMessage() 和 Looper.loop() 这两个方法。

它的简单逻辑就是如果当前 MQ 中存在 msg (即待处理消息)，就将这个消息出队，然后让下一条消息成为mMessages，否则就进入一个阻塞状态，一直等到有新的消息入队。在 loop() 方法中，每当有一个消息出队，就将它传递到 msg.target 的 dispatchMessage() 方法中，那这里 msg.target 又是什么呢？其实就是 Handler 的对象。可以在 sendMessageAtTime( ) 方法中看到。但是此处不同的是 handler 的 dispatchMessage 方法是在创建 Handler 时所使用的 Looper 中执行的，这样就成功的将逻辑代码切换到指定的线程中去了。

来看一下 Handler 中的 dispatchMessage() 以及 Handler 处理消息的过程。

```java
public void dispatchMessage(Message msg) {  
    if (msg.callback != null) {  // 对应着 Handler 的 post 用法
        handleCallback(msg);  
    } else {  // 对应着 handler 的 sendMessage 方法
        if (mCallback != null) {  
            if (mCallback.handleMessage(msg)) {  
                return;  
            }  
        }  
        handleMessage(msg);  
    }  
}  
```

我们总结出 Handler 处理消息的过程: 首先检查 Message 的 callback 是否为 null，不是 null 的话就通过 handleCallback 来处理消息。Message 的 callback 是一个 Runnable 对象。实际上就是 **Handler 的 post** 方法所传递的Runnable 参数。该种写法可以参见 **实例一。**

其次，检查 mCallback 是否为 null，不为 null 则调用 mCallback 的 handleMessage() 方法，否则直接调用 Handler 的handleMessage() 方法，并将消息对象作为参数传递过去。Callback 是一个接口：

```java
public interface Callback{
  public boolean handleMessage(Message msg);
}
```

该种写法可以参见**实例二。**

## 六. 主线程的消息循环

Android 主线程就是 ActivityThread，入口方法为 main。在 main 中系统会通过 Looper.prepareMainLoper() 来创建主线程的 Looper 以及 MessageQueue，并通过 Looper.loop() 来开启主线程的消息循环。

主线程的消息循环开启之后，ActivityThread 还需要一个 Handler 来和消息队列进行交互，这个 Handler 就是 ActivityThread.H,它内部定义了一组消息类型包含了四大组件的启动和停止过程。

## 七. 引申阅读

另外除了发送消息之外，我们还有以下几种方法可以在子线程中进行 UI 操作：

- View 的 post() 方法。
- Activity 的 runOnUiThread() 方法。
- 思考一下为什么建议在 viewHolder 前面添加 static 方法。

## 八. 参考文章

- [Android异步消息处理机制完全解析，带你从源码的角度彻底理解](http://blog.csdn.net/guolin_blog/article/details/9991569)

- 《Android 开发艺术探索》

- [Handler 的基本用法解析](http://blog.csdn.net/iispring/article/details/47115879)

  ​