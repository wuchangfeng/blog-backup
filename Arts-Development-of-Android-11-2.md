---
title: Arts-Development-of-Android-11-2

date: 2016-08-17 20:56:39
categories:
description:
feature:
---

本篇介绍 Android 中的线程池 ThreadPoolExecutor 相关概念。

<!--more-->

## 一. Android 中的线程池

1. 重用线程池中的线程,避免因为线程的创建和开销所代理的性能影响。
2. 能够有效控制线程池的最大并发数,避免互相抢占系统资源所导致组赛。
3. 能够对线程进行简单的管理,提供定时执行以及指定循环时间间隔等。

## 二. ThreadPoolExecutor 

Android 中的线程池都是直接或者间接来配置 ThreadPoolExecutor，其概念来源于 Java 中的 Executor。

它线程池的正真实现者。下面是一个比较常用的构造方法:

```java
Public ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long KeepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         ThreadFactory threadFactory)
```

**参数解释如下**：

* corePoolSize:核心线程数,默认情况下,**核心线程会在线程池中一直存活**,即使处于闲置状态。但是也可以通过将 allowCoreThreadTimeout 属性设置为 true,那么闲置的核心线程也会有超时策略。


* maximumPoolSize:所能容纳最大的线程池数目,超过之后,后续任务会被阻塞。


* keepAliveTime:**非核心线程**闲置时的超时时长。超过这个时长，非核心线程会被回收。


* unit:用于指定 keepAliveTIme 的时间单位。


* workQueue:线程池中的任务队列，通过线程池中的 **execute** 方法提交的 Runnable 对象会存储在这个参数中。


* threadFactory:线程工厂,为线程池提供创建新线程的功能。ThreadFactory 是一个接口，提供一个方法: Thread newThread(Runnable r)。

ThreadPoolExecutor 执行任务时遵循的规则如下规则：

* 如果线程池中的线程数量未达到核心线程的数量，那么会直接启动一个核心线程来执行任务；
* 如果线程数量达到或者超过核心线程的数量，那么任务会被直接插入到任务队列中排队等待执行；
* 如果由于任务队列已经满了，无法插入的话，这时候如果线程数量未达到规定的最大值， 那么会立刻启动一个非核心线程来执行任务；
* 如果线程数量已经达到最大值，那么就会拒绝执行此任务，ThreadPoolExecutor 会调用 RejectedExecutionHandler 的 rejectedExecution 方法来通知调用者。

另外如果想看 **AsyncTask** 中的 ThreadPoolExecutor 的配置是怎么样的，可以参见 《Android 开发艺术探索》 P409.

## 三. 线程池的分类

### 3.1 FixedThreadPool

线程**数量固定**的线程池。当线程处于空闲状态时，他们不会被回收，除非线程池被关闭。当所有线程都处于活动状态时候，新任务处于等待状态。直到有线程空闲出来。**FixedThreadPool 只有核心线程**，并且核心线程不会被回收。这样就加快了它的反应速度。

### 3.2 ScheduledThreadPool

核心线程数没有限制。非核心线程闲置则会被回收。主要用于执行定时任务和具有固定周期的重复任务。

### 3.3 SingleThreadExecutor

**只有一个核心线程**。确保所有任务都在同一个线程中按顺序执行。这使得任务之间不需要处理线程同步的问题。

### 3.4 CachedThreadPool

只有非核心线程。并且线程数量不固定，可以说无限多个。空闲线程都有超时机制。这种线程池比较适合执行大量的耗时较少的任务。

线程池典型使用方法：

``` java
private void runThreadPool() {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
            }
        };
		
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
        fixedThreadPool.execute(command);
        
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(command);
        
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(4);
        // 2000ms后执行command
        scheduledThreadPool.schedule(command, 2000, TimeUnit.MILLISECONDS);
        // 延迟10ms后，每隔1000ms执行一次command
        scheduledThreadPool.scheduleAtFixedRate(command, 10, 1000, TimeUnit.MILLISECONDS);

        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(command);
```



## 四. 参考和引申阅读

* 《Android 开发艺术探索》11章
* [[ThreadPoolExecutor源码学习笔记](http://extremej.itscoder.com/threadpoolexecutor_source/)](http://extremej.itscoder.com/threadpoolexecutor_source/)

