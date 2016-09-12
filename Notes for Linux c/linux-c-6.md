---
title: System V 进程间通信之信号量机制
date: 2016-05-13 18:30:18
tags: linux-c
categories: About Linux
---

信号量通信机制主要用来实现进程间同步，避免并发访问共享资源。信号量可以标识系统可用资源的数量。通常所说的创建一个信号量实际上是创建了一个信号集合，在这个信号量集合中，可以有多个信号量。

信号量采取的存储方式是数组，为什么这里要用数组来存储信号量？相比较消息队列而言呢？


<!-- more -->

### 一 . 概念介绍

信号量是一个特殊的变量，程序对其访问都是**原子操作**，且只允许对它进行等待（即P(信号变量))和发送（即V(信号变量))信息操作。最简单的信号量是只能取0和1的变量，这也是信号量最常见的一种形式，叫做二进制信号量。而可以取多个正整数的信号量被称为通用信号量。这里主要讨论二进制信号量。

### 二 . 核心函数

**semget函数**

创建一个信号量集合：

	int semget(key_t key, int num_sems, int sem_flags);
  
第一个参数 key 是整数值（唯一非零），不相关的进程可以通过它访问一个信号量，它代表程序可能要使用的某个资源，程序对所有信号量的访问都是间接的，程序先通过调用 semget 函数并提供一个键，再由系统生成一个相应的信号标识符（ semget 函数的返回值），只有 semget 函数才直接使用信号量键，所有其他的信号量函数使用由 semget 函数返回的信号量标识符。如果多个程序使用相同的key值，key 将负责协调工作。

第二个参数 num_sems 指定需要的信号量数目，它的值几乎总是1。

第三个参数 sem_flags 是一组标志，当想要当信号量不存在时创建一个新的信号量，可以和值IPC_CREAT做按位或操作。设置了 IPC_CREAT 标志后，即使给出的键是一个已有信号量的键，也不会产生错误。而IPC_CREAT | IPC_EXCL则可以创建一个新的，唯一的信号量，如果信号量已存在，返回一个错误。

**semop函数**

用来操作信号量集合：

	int semop(int sem_id, struct sembuf *sem_opa, size_t num_sem_ops); 
 
sem_id 是由 semget 返回的信号量标识符，sembuf 结构的定义如下：

``` c
	struct sembuf{  
    	short sem_num;//除非使用一组信号量，否则它为0  
    	short sem_op;//信号量在一次操作中需要改变的数据，通常是两个数，一个是-1，即P（等待）操作，  
                    //一个是+1，即V（发送信号）操作。  
    	short sem_flg;//通常为SEM_UNDO,使操作系统跟踪信号，  
                    //并在进程没有释放该信号量而终止时，操作系统释放信号量  
	}; 
```
**semctl函数**

该函数用来直接控制信号量信息，它的原型为：

	int semctl(int sem_id, int sem_num, int command, ...);
  
如果有第四个参数，它通常是一个union semum结构，定义如下：

``` c
	union semun{  
    	int val;  
    	struct semid_ds *buf;  
   	unsigned short *arry;  
	};  
```

前两个参数与前面一个函数中的一样，command 通常是下面两个值中的其中一个
SETVAL：用来把信号量初始化为一个已知的值。p 这个值通过union semun中的 val 成员设置，其作用是在信号量第一次使用前对它进行设置。
IPC_RMID：用于删除一个已经无需继续使用的信号量标识符。

union 即在 C 中实现 C++ 的多态的概念，非常有用的操作。

### 三 . 实例代码

下面的实例代码用信号量机制来实现操作系统的 P V 机制的仿真程序，即用 sleep() 来模拟一些耗时操作，关键注释在代码中。

本质上这个 Demo 还是一个信号量机制的通信程序，通信最核心的还是之前提到过的 ftok 即 key 标识。

``` c
#include<stdio.h>
#include<sys/types.h>
#include<sys/sem.h>

union semun // realize C++ in c
{
    int val;// 1
    struct semid_ds *buf;// 2
    ushort *array;// 3
};
/*
*fun(int n,union semun)
* n can be 1 ,2,3 like:
* ushort s[2];
*	
* s[0]=1;s[1]=3;fun(3,s);
*/
int main()
{
    key_t key;
    int semid,menu;
    union semun arg;
    short semarray[]={1};
    arg.array=semarray;// arg is an instance of union semun
    key=ftok(".",12345);
    semid=semget(key,1,IPC_CREAT|0770);
    semctl(semid,0,SETALL,arg);// focus on SETALL and arg
    struct sembuf sops[2];// sops is struct and all his child should be set 
    sops[0].sem_num=0;
    sops[0].sem_op=1;// v
    sops[0].sem_flg=SEM_UNDO;
   
    sops[1].sem_num=0;
    sops[1].sem_op=-1; // p
    sops[1].sem_flg=SEM_UNDO;

    do{
        printf("1.p操作\n");
        printf("2.v操作\n");
        printf("3.退出\n");
        printf("请输入你的选择：");
        scanf("%d",&menu);
        if(menu==1)// p
        {
			
            semop(semid,sops+1,1);// operate the sem ,p operation
        }
        else if(menu==2)
        {
			
            semop(semid,sops,1);// operate the sem ,v operation
        }
    }while(menu!=3);
    return 0;
}
```

``` c
#include<stdio.h>
#include<sys/types.h>
#include<sys/sem.h>
union semun
{
    int val;
    struct semid_ds *buf;
    ushort *array;
};
int main()
{
    key_t key;
    int semid;
    union semun arg;
    short semarray[]={1};
    arg.array=semarray;
    key=ftok(".",12345);
    semid=semget(key,1,IPC_CREAT|0770);
    semctl(semid,0,SETALL,arg);
    
    struct sembuf sops[2];
    
    sops[0].sem_num=0;
    sops[0].sem_op=1;
    sops[0].sem_flg=SEM_UNDO;
    
    sops[1].sem_num=0;
    sops[1].sem_op=-1;
    sops[1].sem_flg=SEM_UNDO;
    while(1)
    {
        printf("正在执行p操作......\n");
        semop(semid,sops+1,1);
        printf("已执行完p操作,正在使用资源......\n");
        sleep(3);
        printf("资源使用完毕，正在执行v操作......\n");
        semop(semid,sops,1);
        printf("v操作执行完毕\n\n");
        sleep(3);
    }
    return 0;
}
```