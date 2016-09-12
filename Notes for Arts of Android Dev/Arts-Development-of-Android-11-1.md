---
title: Arts-Development-of-Android-11-1
date: 2016-08-17 19:58:39
toc: true
categories: 读书笔记
tags: Android
description:
feature:
---

本篇介绍 AsyncTask 的使用方法和工作原理。

<!--more-->

## 一 . 概述

1. 线程分为主线程和子线程,子线程执行耗时操作。
2. 除了 Thread 之外,AsyncTask 和 IntentService 还有 HandlerThread 也是一些特殊的线程。
3. AsyncTask 封装了线程池和 Handler ，方便在子线程中更新 UI。
4. HandlerThread 是一种具有消息循环的线程,在其内部可以使用 Handler。
5. IntentService **是一个服务**,其内部是 HandlerThread 来执行任务,完成后自动退出。虽然其作用很像**一个后台线程**,但是最终其还是一个服务。不容易被系统杀死，从而保证后台任务的执行。
6. Android 中也引入了线程池的概念，这样可以减少频繁的创建和销毁线程。
7. Android 中线程也分为主线程和子线程,它们之间的作用区别很明显。

## 二 . AsyncTask 使用

AsyncTask 的使用方式如下实例 Demo:

```java
class DownloadTask extends AsyncTask<URL, Integer, Boolean> {  
  
    @Override  
    protected void onPreExecute() {  
        progressDialog.show();  
    }  
  
    @Override  
    protected Boolean doInBackground(Void... params) {  
        try {  
            while (true) {  
                int downloadPercent = doDownload();  
                publishProgress(downloadPercent);  // 子线程切换至 UI 线程
                if (downloadPercent >= 100) {  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            return false;  
        }  
        return true;  
    }  
  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        progressDialog.setMessage("当前下载进度：" + values[0] + "%");  
    }  
  
    @Override  
    protected void onPostExecute(Boolean result) {  // 提示任务执行结果
        progressDialog.dismiss();  
        if (result) {  
            Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();  
        } else {  
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();  
        }  
    }  
}  
```

而我们调用时候只需要如下简单语句即可:

```java
new DownloadTask().execute(url1，url2，url3);  
```

 **对于上述示例，我们注意以下几点**:

1. doInBackgroud 是在**线程池中执行的**。
2. onProgressUpdate 用于更新界面中的下载进度，运行在主线程中，当 **publishProgress** 被调用时，此方法就会**被调用。**不像 Handler 中需要发送和接受消息来切换线程了。
3. 当下载任务完成后，onPostExecute 方法就会**被调用。** 它也是运行在主线程中。

## 三. 源码分析

程序的执行入口是 new AsyncTask().execute() 我们自然要看一下 AsyncTask 的构造方法执行了哪些初始化工作：

```java
public AsyncTask() {  
  	// mWorker 对象是一个 Callable 对象
    mWorker = new WorkerRunnable<Params, Result>() { 
      	// 该方法会在线程池中执行
        public Result call() throws Exception {  
          	// 表示任务已经执行过了
            mTaskInvoked.set(true);  
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);  
          	// 调用 doInBackgroud 方法并将结果返回至 postResult
            return postResult(doInBackground(mParams));  
        }  
    };  
  	// mFuture 对象是一个 FutureTask 对象
    mFuture = new FutureTask<Result>(mWorker) {  
        @Override  
        protected void done() {  
          //...
    };  
}  
```

容易看出来初始化了两个对象。

接着我们进入 execute() 方法看一看，注意这里传入了 sDefaultExecutor 和 params：

```java
public final AsyncTask<Params, Progress, Result> execute(Params... params) {  
    return executeOnExecutor(sDefaultExecutor, params);  
} 
```

进入 executeOnExecutor( ) 方法看一看：

```java
public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,  
        Params... params) {  
  	// task 状态的判断
    if (mStatus != Status.PENDING) {  
        switch (mStatus) {  
            case RUNNING:  
                throw new IllegalStateException("Cannot execute task:"  
                        + " the task is already running.");  
            case FINISHED:  
                throw new IllegalStateException("Cannot execute task:"  
                        + " the task has already been executed "  
                        + "(a task can be executed only once)");  
        }  
    }  
    mStatus = Status.RUNNING;  
    onPreExecute();  // 证明 onPreExecute 第一个被执行还在 UI 线程中...
    mWorker.mParams = params;  // params 传入 mWorker 而其与 mFuture 有关
    exec.execute(mFuture);  // 重点
    return this;  
}  
```

上述代码中我们并没有看到 doInBackground() 方法。而唯一的突破口就是  exec.execute(mFuture) 了。我们可以很清晰的看到 exec 实际上是 sDefaultExecutor ，这是什么？我们自然要去该对象的 execute() 方法中看看了。在上面代码中我们将 mFuture 传入进了 execute() 方法中。FutureTask 是一个并发类，传入进去之后，其充当了 Runnable 对象：

```java
public static final Executor SERIAL_EXECUTOR = new SerialExecutor();  
// 实际上是一个串行的线程池用来排队的
private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR; 
private static class SerialExecutor implements Executor {
  	// 定义任务对列，指定类型为 Runnable 对象
    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();  
    Runnable mActive;  
  	// 这里的 execute 就开始在子线程中执行了
    public synchronized void execute(final Runnable r) {  
      	// 插入到任务队列 mTask 中
        mTasks.offer(new Runnable() {  
            public void run() {  
                try {  
                  	// 突破口
                    r.run();  
                } finally {  
                    scheduleNext();  
                }  
            }  
        });  
      // 如果这时候没有活动的 Asynctask 任务就会执行下一个任务
        if (mActive == null) {  
            scheduleNext();  
        }  
    }  
 
    protected synchronized void scheduleNext() { 
      // 这里可以看出 AT 是串行执行的，队列的 poll() 方法嘛
        if ((mActive = mTasks.poll()) != null) { 
          	// THREAD_POOL_EXECUTOR 是用来执行任务的
            THREAD_POOL_EXECUTOR.execute(mActive);  
        }  
    }  
}  
```

在上面的 sDefaultExecutor 实际上是一个串行的线程池。一个进程中所有的 AsyncTask 全部在这个串行的线程池中排队执行。

接着我们继续追踪 run() 直到发现这样一句:

```java
result = callable.call();  
```

这句是一开始初始化 AsyncTask 中构造函数中的。原来是一个**回调机制**啊，再次拿出来分析分析：

```java
 	// mWorker 对象是一个 Callable 对象
    mWorker = new WorkerRunnable<Params, Result>() { 
      	// 该方法会在线程池中执行
        public Result call() throws Exception {  
          	// 表示任务已经执行过了
            mTaskInvoked.set(true);  
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);  
          	// 调用 doInBackgroud 方法并将结果返回至 postResult
            return postResult(doInBackground(mParams));  
        }  
    };  
```

接着进入 postResult( ) 方法中看一看:

```java
private Result postResult(Result result) {  
  	// 消息携带两个一个常量和一个执行结果
    Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT,  
            new AsyncTaskResult<Result>(this, result));
  	// 发送消息
    message.sendToTarget();  
    return result;  
}  
```

这里使用 sHandler 对象发出了一条消息，消息中携带了 MESSAGE_POST_RESULT 和一个表示任务执行结果的 AsyncTaskResult 对象。这个 sHandler 对象是 InternalHandler 类的一个实例，那么这条消息肯定会在InternalHandler 的 handleMessage() 方法中被处理。InternalHandler 的源码如下所示：

```java
// 静态类
private static class InternalHandler extends Handler {  
    //...
    @Override  
    public void handleMessage(Message msg) {  
        AsyncTaskResult result = (AsyncTaskResult) msg.obj;  
        switch (msg.what) {  
            case MESSAGE_POST_RESULT:  
                // 调用 finish 方法
                result.mTask.finish(result.mData[0]);  
                break;  
            case MESSAGE_POST_PROGRESS: 
               // 解释 publishProgress() 方法可以从子线程切换到UI线程
                result.mTask.onProgressUpdate(result.mData);  
                break;  
        }  
    }  
}  
```

发现 Handler 是一个静态类。是为了能够将执行环境从**子线程切换到主线程中。**由于静态类的关系，更进一步要求 AsyncTask 的类必须在主线程中加载(**这里不是很明白。**静态成员会在加载类的时候进行初始化，这样同一个进程中的线程就可以共享资源了？)。sHandler 在收到 MESSAGE_POST_RESULT 后会调用 fininsh() 方法：

```java
private void finish(Result result) {  
    if (isCancelled()) {  
        onCancelled(result);  
    } else {  
        onPostExecute(result);  
    }  
  	// 状态标示
    mStatus = Status.FINISHED;  
}  
```

这里就比较简单了。分取消与否调用 onCancelled() 和 onPostExecute() 方法。

## 四. 总结分析

AsyncTask 封装了 Thread 和 Handler, 通过 AsyncTask 可以更加方便的执行后台任务以及在主线程中访问 UI,但是 AsyncTask 并不适合进行特别耗时的后台任务。建议采用线程池。

提供了四个核心方法:

1. onPreExecute():在主线程中执行,在异步任务执行前,此方法会被调用,用于做一些准备工作。
2. doInBackground(Params...params):在线程池中执行,此方法用于执行异步任务。
3. onProgressUpdate(Progress...value):在主线程中执行,当后台任务的执行进度发生变化时,此方法会被调用。
4. onPostExecute(Result result):在主线程中执行,在异步任务执行之后,此方法会被调用。
5. onCancelld():当异步任务被取消时,该方法会被调用。

对于使用AsyncTask 有以下几点需要注意的：

1. AsyncTask 的类必须在主线程中加载。
2. AsyncTask 的 **对象** 必须在 **主线程中创建**。
3. execute 必须在 UI 线程中调用。
4. 不要在程序中直接调用上述前四个方法。
5. 在 Android 1.6 之前 AT 是串行执行任务的(可同时执行 5 个任务)，那时候采用线程池来处理并行任务，但是从 3.0 开始为了避免 AT 所带来的并发错误，AT 又采用一个线程来串行执行任务(只能执行 1 个任务)。尽管如此，3.0 之后仍然可以通过 AT 的 executeOnExecutor 方法来**并行**执行任务。对于这一点的实验可以看 《Android 开发艺术探索》P403。
6. 如果要**深究为什么 Android 3.0 以上 AT 默认不并行执行**，可以看[这篇](http://www.jianshu.com/p/a8b1861f2efc?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq)文章。

## 五. 参考文章

- 《Android 开发艺术探索》

- [Android AsyncTask完全解析，带你从源码的角度彻底理解](http://blog.csdn.net/guolin_blog/article/details/11711405)

  ​



