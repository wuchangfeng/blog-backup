---
title: Java 中线程以及其应用
date: 2015-12-12 20:38:32
tags: java
categories: About Java
---

读书笔记之 java 常用线程以及线程在存钱取实例中的应用。

<!-- more -->

[Java线程面试题](http://www.jianshu.com/p/a53e1d01adde)


* volatile关键字在Java 中有什么作用？

当我们使用volatile关键字去修饰变量的时候，所以线程都会直接读取该变量并且不缓存它。这就确保了线程读取到的变量是同内存中是一致的。

### 1. 多线程

并发和并行是两个概念：**并行**指的是同一时刻，有多个指令在多个处理器上同时执行，**并发**是指在同一时刻只能有一条指令执行，但是**多个进程指令被快速轮换执行**，使得宏观上具有多个进程同时执行的效果。
但是对于一个 CPU 而言，同一时刻只能执行一条指令，之所以看上去很多进程在同时工作，因为进程之间切换非常快，感觉上去实在同时工作。现在采用的较多的是抢占式多任务操作策略。

进而引出多线程，多线程为什么会出来，有什么优势？或者应用？

* Thread.currentThread:currentThread 是 Thread **类的静态方法**，该方法总是返回当前正在执行的线程对象。

* getName():该方法是 Thread 类的**实例方法**，该方法返回调用该方法的线程名字。


#### 线程概述

#### 线程的创建和启动

* 继承 Thread 类创建线程类

 * 定义 Thread **子类**，并且重写 Run 方法，里面的执行体代表线程要完成的任务。
 * 创建Thread 子类的实例，即创建了线程对象。
 * 调用线程对象的 start() 方法来启动线程。

注意一点 main() 方法的体代表主线程的执行体。 由于比较简单，实例略。并且由于不是在同一个进程中创建这些线程，故而线程之间并不是共享实例变量。

* 实现 Runnable 接口创建线程类

 * 定义 Runable **接口实现类**，并且重写 run() 方法。
 * 创建 Runable 实现类的实例，并且以此实例作为 Thread 的target 来创建 Thread 对象，该对象才是真正的线程对象。
 * 最后调用start() 方法来启动线程。
 
注意，这里多个线程是共享一个target的，共享变量。 

* 使用 Callable 和 Furture 创建线程
    
``` java
public class CallableThread {
    	public static void main(String[] args) {

        CallableThread ct = new CallableThread();

        FutureTask<Integer> task = new FutureTask<Integer>((Callable<Integer>) ()->{
            int i;
            for ( i = 0; i < 100 ; i++) {
                System.out.println(Thread.currentThread().getName() + " 的循环变量 i 的值：" + i);
            }
            return i;
        });
        
        //另一种写法
        
        FutureTask<Integer> task1 = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        });
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " 的循环变量 i 的值：" + i);

            if (i == 20) {
                new Thread(task,"有返回值的线程").start();
            }
        }
        try {
            System.out.println("子线程返回值：" + task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
  	  }
}
```

先不要管，程序什么意思，只要看程序中的 lambda 表达式，还原之后是什么样的？

**正题**

java5 提供了 Callable的接口，像是 Runnable 的加强版，其提供了 call() 方法作为线程执行体，但是 call() 方法更为牛逼，其一可以有返回值，其二可以抛出异常。但是注意 Callable 接口并不是 Runnable 接口的子接口。所以 Callable 对象不能作为 Thread 的 target。

怎么引出 Future接口的？

Future接口提供了如下方法：

1：boolean cancel():

2：V get(): 返回 Callable 任务里面的 call() 方法返回值，其将导致程序阻塞，必须等到子线程结束后，才会得到返回值。

3：boolean isCancelled():

4：boolean isDone：

创建并启动有返回值的线程步骤如下：

1：创建 Callable 接口的实现类，实现 call(),并将其作为线程执行体，且该 call() 方法有返回值，再创建 Callable 实现类的实例。

2： 使用 FurtureTask 类来包装 Callable 对象，该 FurtureTask 对象**包装**了该 Callable 对象的 call() 对象的 **call() 方法的返回值**。

3：使用 **FutureTask 对象作为 Thread 对象的 target** 创建并且启动线程。


4：调用 FutureTask 对象的** get() 方法来获取子线程执行后**的返回值。明显其会导致阻塞。


#### 线程的生命周期

* 新建

当创建一个线程之后，虚拟机就会为其分配内存，初始化其成员变量的值，此时没有表现出任何动态特征，处于新建状态。

* 就绪

当线程对象调用了 start() 方法之后，处于就绪状态，虚拟机会为其创建方法调用栈和程序计数器，也还没有开始运行，等待虚拟机的调度。

* 运行

获得 Cpu 即处于运行状态，执行线程方法体。

* 阻塞
  
 线程 sleep(),主动放弃所占有资源

 线程调用了一个阻塞式Io方法，在该方法返回时，处于阻塞。

 线程在等待某个通知 notify()
 
 程序将线程挂起

 线程试图获得同步监视器，但是其被其他线程所占有。  



* 死亡
 
 run() 或者 call() 方法执行完毕，正常结束。
 
 线程抛出一个未捕获的异常。
 
 直接调用线程的 stop() 来结束，容易导致死锁，不推荐。

### 2. 线程控制

* join线程

线程提供了让一个线程等待另一个线程完成的方法，即 join()。

* 后台线程

守护进程，关键词：
	
	thread.setDaemon();

* 线程睡眠(sleep) 

让当前线程暂停一段时间，并进入**阻塞状态**，可以通过 Thread 的静态方法 sleep() 实现。
  
* 线程让步(yield)

同样也是让当前线程暂停一段时间，但是其处于**就绪状态**，让系统的线程调度器重新调度一次。

* 改变优先级

### 3. 线程同步(重要)
使用多个线程对同一个变量进行访问，就有可能出现异常，没有控制好先后。

* 同步代码块

``` java
synchronized(obj){
    
}
```
上面的 obj 就是同步监视器，个人感觉就是 多个线程都要操作的对象了。当然同步代码块执行完毕之后，自动释放同步监视器。

* 同步方法

``` java
public synchronized void XX(){
		
}
```

很显然，同步方法的同步监视器就是调用该方法的对象即 this。

* 释放同步监视器

取钱的例子如下，模拟两个人对并发同一账户取钱的例子：

``` java
class Accouts{

    private String accountNo;
    private double balance;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Accouts() {
    }

    public Accouts( String accountNo,double balance) {
        this.balance = balance;
        this.accountNo = accountNo;
    }

    @Override
    public int hashCode() {
        return accountNo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
       // return super.equals(obj);
        if (this == obj)
            return true;
        if (obj != null && obj.getClass() == Accouts.class){

            Accouts target = (Accouts) obj;
            return target.getAccountNo().equals(accountNo);
        }

        return false;

    	}
	}

	class DrawThread extends Thread {

    private Accouts accounts;
    private double drawAccount;

    public DrawThread(String name, Accouts accounts, double drawAccount) {
        super(name);
        this.accounts = accounts;
        this.drawAccount = drawAccount;
    }

    public void run() {
        synchronized (accounts) {
            if (accounts.getBalance() >= drawAccount) {
                //吐出钞票
                System.out.println(getName() + "取钱成功，金额为 ：" + drawAccount);
                // 当前线程暂停，引起线程切换，模拟情形
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //修改账户余额
                accounts.setBalance(accounts.getBalance() - drawAccount);
                System.out.println("\t余额为 ：" + accounts.getBalance());
            } else {

                System.out.println(getName() + "取钱失败，余额不足");
            }
    	    }
    	}
	}
	//测试
	public class DrawTest {

    	public static void main(String[] args) {
    	    //创建一个账户
    	    Accouts acc = new Accouts("123456",1000);
    	    //模拟两个线程对同一个账户取钱
    	    new DrawThread("A",acc,800).start();
    	    new DrawThread("B",acc,800).start();

    	}
}
```


* 同步锁(LOCK)

Lock 是控制多个线程对共享资源进行访问的工具。通常，锁提供了对共享资源的独占访问，每次只有一个线程对 Lock 对象加锁，线程开始访问共享资源之前应先获得 Lock 对象。注意 Lock 是要显式按照顺序释放锁对象的。

这个详细可以 google 一下。

在实现线程安全的控制中，比较常用的是 ReentrantLock(可重入锁，即可以锁中有锁)，使用该 Lock 对象可以显示的加锁，释放锁，通常使用 ReentrantLock 格式如下：

``` java
class X{
// 定义锁对象
private final ReentrantLock lock = new ReentrantLock();
// ...
// 定义需要保证线程安全的方法
    public void m(){
        
        //加锁
        lock.lock();
        try{
            //需要保证线程安全的代码
        }
        //使用 finally 来保证释放锁
        finally {
            lock.unlock();
        }
        
}
```



* 死锁

当两个线程互相等待对方释放同步监视器时就会发生死锁，java 虚拟机没有检测，也不会采取监测措施，所以多线程编程时要自己控制好。

怎样才能避免死锁？操作系统上的经典问题了。

### 4. 线程通信(重要)

* 传统的线程通信

可借助于 Object 类的 wait(),notify()和notifyAll()三个方法，三个方法不属于 Thread 类，而属于 Object类，但这三个方法必须由同步监视器来调用，可分为两种：
  
   1： 使用 Synchronized 修饰的同步方法 ，该类默认实例(this)就是同步监视器，可以直接调用。

   2： 对于 Synchronized 修饰的同步代码块，其括号里面的就是同步监视器，用其调用即可。  

三个方法如下解释：

   1：wait(),当前线程等待，直到其他线程调用该同步监视器的 notify()或者notifyAll()方法来唤醒该进程
   2：notify(),唤醒在此同步监视器上等待的单个线程。如果所有都在等待，则随机选取唤醒之。同下。
   3：notifyAll(),唤醒在当前同步监视器上等待的所有线程。只有当前线程放弃对同步监视器的锁定后(wait())，才执行新的线程。



* 使用 Conditon 控制线程通信

 当使用 Lock  对象来保证同步时，使用 condition 类来保持协调，它可以使那些已经得到 Lock 对象却无法继续执行的线程释放 Lock 对象，condition 对象也可以唤醒其他处于等待状态的线程。

1：await(): 类似于隐式同步监视器上的 wait() 方法，让当前线程等待，直到其他线程调用该 condition 的 signal() 或者 signalAll() 来唤醒该线程。

2：signal():唤醒在此 Lock 对象上等待的单个线程。如果所有，则唤醒任意一个。只有当前线程放弃对 该 Lock 对象的锁定后，才可以执行被唤醒的线程。

3：signalAll():唤醒在此 Lock对象的所有线程。同样只有当前线程放弃该 Lock 之后，才可以执行被唤醒的进程。


同样根据上一次取钱的例子，要钱变了，系统中有两个线程，分别代表存钱者和取钱者，不停的存钱取钱。不允许存钱者连续两次存钱，也不许取钱者连续两次取钱。写出代码如下：

``` java
class Accouts{
    
    //显示定义Lock对象
    private final Lock lock = new ReentrantLock();
    //获得指定Lock对象对应的Condition
    private final Condition cond = lock.newCondition();
    //标示账户中是否已有存款的flag
    private boolean flag = false;
    private String accountNo;
    private double balance;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    // 账户余额不能乱改，只为其提供 set
    public double getBalance() {
        return this.balance;
    }
    
    public Accouts() {
    }

    public Accouts( String accountNo,double balance) {
        this.balance = balance;
        this.accountNo = accountNo;
    }

    //取钱的方法
    public void draw(double drawAccount) {

        lock.lock();

        try {
            //如果 flag 为假，则表明账户中还没有人存钱进去，取钱方法阻塞
            if (!flag) {
                // 导致当前线程等待，直到其他线程调用 signal() 或者 signalAll() 方法唤醒
                cond.await();

            } else {
                // 取钱操作
                System.out.println(Thread.currentThread().getName() + "取钱：" + drawAccount);
                balance -= drawAccount;
                System.out.println("账户余额为：" + balance);
                // 将标示账户是否已有存款的 flag 设为 false
                flag = false;
                // 唤醒其他线程，这里不要想错了，其唤醒的是那些存钱被阻塞的线程
                cond.signalAll();
            }

        } catch (InterruptedException ex) {

        } finally {
            lock.unlock();
        }
    }

    // 存钱的方法

    public void deposit(double depositAmount){

        lock.lock();

        try {
                // 如果 flag 为真，表明当前已有人存钱进去，则存钱方法阻塞
                if (flag)
                    cond.await();
                else{
                    // 执行存钱操作
                    System.out.println(Thread.currentThread().getName() + "存款：" + depositAmount);
                    balance += depositAmount;
                    System.out.println("账户余额为：" + balance);
                    flag = true;
                    //唤醒那些等待着取钱的进程
                    cond.signalAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                lock.unlock();
            }
        }
    
    @Override
    public int hashCode() {
        return accountNo.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
       // return super.equals(obj);
        if (this == obj)
            return true;
        if (obj != null && obj.getClass() == Accouts.class){

            Accouts target = (Accouts) obj;
            return target.getAccountNo().equals(accountNo);
        }
        return false;
    	}
	}

	class DrawThread extends Thread{

    private Accouts accouts;
    private double drawAmount;

    public DrawThread(String name, Accouts accouts, double drawAmount) {
        super(name);
        this.accouts = accouts;
        this.drawAmount = drawAmount;
    }
    //重复100次取钱操作
    public void run(){

        for (int i = 0; i < 100; i++) {
            accouts.draw(drawAmount);
        }
    	}
	}

	class DepositThread extends Thread{

    private Accouts accouts;
    private double depositAmount;

    public DepositThread(String name, double depositAmount, Accouts accouts) {

        super(name);
        this.depositAmount = depositAmount;
        this.accouts = accouts;
    }
    //重复100次存钱操作
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            accouts.deposit(depositAmount);
        }
    	}
	}
	//测试
	public class DrawTest {

    public static void main(String[] args) {
        //创建一个账户
        Accouts acc = new Accouts("123456",0);
        // 
        new DrawThread("取钱者",acc,800).start();
        //
        new DepositThread("存钱者甲",800,acc).start();
        new DepositThread("存钱者乙",800,acc).start();
        new DepositThread("存钱者丙",800,acc).start();

    	}
}
```

结果：

	存钱者乙存款：800.0
	账户余额为：800.0
	取钱者取钱：800.0
	账户余额为：0.0
	存钱者乙存款：800.0
	账户余额为：800.0
	取钱者取钱：800.0
	账户余额为：0.0
	存钱者乙存款：800.0
	账户余额为：800.0
	取钱者取钱：800.0
	账户余额为：0.0
	存钱者乙存款：800.0
	账户余额为：800.0
	取钱者取钱：800.0
	账户余额为：0.0
	存钱者乙存款：800.0
	账户余额为：800.0



* 使用阻塞队列控制线程通信

 java5 提供了一个 BlockingQueue 接口，其作为线程同步的工具：当生产者视图向 BQ 放入元素时，如果队列已满，则被阻塞，当消费者线程试图取数据时，如果队列已空，则被阻塞。一个简单的实例代码如下：
	
``` java
public class BlockingQueueTest {
public static void main(String args[]) 
      throws Exception{
        BlockingQueue<String> bq = new ArrayBlockingQueue<String>(1);
        bq.put("Java");
        bq.put("Java");
        bq.put("Java");
    	}
}
```
结果并没有引发异常，为什么呢？

### 5. 线程池(重要)

为什么使用线程池？有什么好处？


答：线程池在系统启动时即**创建大量空闲的线程**，程序将一个Runnable对象或者Callable()对象传给线程池，线程池就会**启动一个线程来执行它们的run()或者call() 方法**，当这些方法结束时，线程不会死亡，而是再次返回线程池中成为**空闲状态**，等待执行下一个 Runnable对象或者 Callable() 对象的run()或者call()方法。

使用线程池可以有效地控**制系统中并发线程的数量**，提高JVM效率。预防 JVM 崩溃。

使用 Executors 工厂类产生线程池，包含以下几个静态工厂方法来创建线程池：

 * newCachedThreadPool():
 * newFixedThreadPool(int nThreads):
 * newSingleThreadExcutor():
 * newScheduledThreadPool(int corePoolSize):
 * newSingleThreadScheduledExecutor():
 * ExecutorService new workStealingpool(int parallelism):
 * ExecutorService newWorkStealingPool():


前三个方法返回一个 ExecutorService 对象，一个线程池，可以执行 Runnable/Callable 对象所代表的线程；中间的是 ExecutorService 子类即 ThreadScheduledExecutorService 线程池，可以指定延迟后执行线程任务；最后的两个是 Java 8 新增加的，可以充分利用多核 CPU 的威力。


废话不多说，实例上一个：

``` java
public class ThreadPoolTest {

    public static void main(String args[]) throws Exception{
        // 创建一个具有固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(6);
        // 用 lambda 表达式创建 Runnable 对象
        Runnable target = () ->{

          for (int i=0;i < 10;i ++){
              System.out.println(Thread.currentThread().getName() + "的i值为：" + i);
          }
        };
        // 下面这个，就是上面的lambda的正常写法
        Runnable target1 = new Runnable() {
            //只包含一个无参数的 run 方法！！！
            @Override
            public void run() {

            }
        };
		// 向线程池中提交两个线程
        pool.submit(target);
        pool.submit(target);
        // 关闭线程池
        pool.shutdown();
    	}
}
```

其创建过程如下：

1. 调用 Executor 类的静态工厂方法创建一个 ExecutorSercice 对象，该对象代表一个线程池
2. 创建线程实例 Callable 或者 Runnable 实例
3. 调用 ExecutorSercice 对象的 submit 对象的 submit 方法来提交实例
4. 当不想提交任何任务时，使用 ExecutorSercice 对象的 shutdown来关闭线程池

这里我们自然要回顾一下怎么利用 lambda 创建 Runnable 对象了？以及线程池到底什么作用？

### 6. 线程相关类

ThreadLocal 从另一个角度来解决多线程的并发访问，ThreadLocal 将需要并发访问的资源**复制多份**，每一个线程拥有一份资源，每个线程都有自己的副本，自然没有必要对该变量进行同步了。

ThreadLocal 并不能代替同步机制，两者面向的问题领域不同，**同步机制为了同步多个线程对相同资源并发上的访问**，是多个线程之间通信的有效方式;ThreadLocal 是为了**隔离多个线程之间的数据共享**，从而本质上避免多个线程对共享资源之间的竞争，那么更不需要同步一说了。

自然问题出来了，最后加入都对这一份资源做了修改，最后怎么同步呢？

介绍：ThreadLocal 即线程局部变量的意思，功能即：为每一个使用该变量的线程都提供一个变量的副本，每个线程之间可以独立的改变这些变量值，并且之间不会冲突。

ThreadLocal 提供了以下几个方法：

1： T get():返回此线程局部变量中当前线程副本中的值。
2： void remove():删除此线程局部变量中当前线程的值。
3： void set(T value):设置此线程局部变量中当前线程副本中的值。

实例如下：

``` java
class Accout {

    public String getName() {
        return name.get();
    }

    public void setName(String str) {
        this.name.set(str);
    }

    /**
     * 定义一个ThreadLocal类型的变量，该变量将是一个线程局部变量，每隔线程都会保留该变量的一个副本。
     */
    private ThreadLocal<String> name = new ThreadLocal<>();

    public Accout(String str){
       this.name.set(str);
        //下面代码用于访问当前线程的 name 副本的值。
        System.out.println("---" + this.name.get());
    }

	}

	class MyTest extends Thread{
    // 定义一个 Accout 类型的成员变量
    private Accout accout;

    public MyTest(Accout accout , String name) {

        super(name);
        this.accout = accout;

    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            // i = 6 时将账户名替换成当前线程名
            if (i == 6){

                accout.setName(getName());
            }
            System.out.println(accout.getName() + "账户的i值：" + i);
        }
    	}
	}

	public class hreadLocalTest {
    	public static void main(String[] args) {
        //启动时，两个线程公用一个 accout
        Accout at = new Accout("初始名");

        new MyTest(at,"线程甲").start();
        new MyTest(at,"线程乙").start();
    	}
} 
```

结果：

	null账户的i值：0
	null账户的i值：1
	null账户的i值：2
	null账户的i值：3
	null账户的i值：4
	null账户的i值：5
	线程甲账户的i值：6
	线程甲账户的i值：7
	线程甲账户的i值：8
	线程甲账户的i值：9
	null账户的i值：0
	null账户的i值：1
	null账户的i值：2
	null账户的i值：3
	null账户的i值：4
	null账户的i值：5
	线程乙账户的i值：6
	线程乙账户的i值：7
	线程乙账户的i值：8
	线程乙账户的i值：9

知道 null 什么意思吗？将其当成账户名的副本就行了。第一和第二个账户分别有一个线程的副本。

### 7.包装线程不安全的集合

ArrayList,LinkedList,HashSet,TreeSet,HashMap,TreeMap,都是**线程不安全的**,即线程并发对集合进行操作时，会不安全。可以使用 Collections 提供的类方法把这些集合包装成线程安全的集合。

 1： <T>Collection<T>synchronizedCollection(Collection<T> c):返回指定的 collection 对应的线程安全的 collection。
 
可以参见疯狂 java 讲义 P758.

如在多线程中，使用线程安全的 HashMap 对象：

``` java
HashMap m = Collections.synchronizedMap(new HashMap());  
```



### 8. 线程安全的集合

粗略的可以参考 P760

从 java5 开始 java.util.concurrent 包下面提供了大量支持高效并发访问的集合接口和接口实现类。粗略的看，这些线程安全集合类，可以分为以下两类：

* Concurrent开头的集合类：ConcurrentHashMap，ConcurrentSkipListMap,ConcurrentSkipListSet,ConcurrentSkipLinkedQueue...

* 以CopyOnWrite开头的集合类，如CopyOnWriteArrayList，CopyOnWriteArraySet。

默认情况下，ConcurrentHashMap 支持16个线程并发写入，java 8 又对该集合进行了更加深入的扩充。
