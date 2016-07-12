---
title: linux 进程管理与程序开发
date: 2016-05-07 07:27:46
tags: linux-c
categories: About Linux
---

进程是 linux 事物管理的基本单元，进程均拥有自己的独立的处理环境与系统资源，进程环境由当前系统及父进程信息决定和组成，系统第一个进程 init 由内核产生，所有其他的进程都是由 fork() 调用产生的。

* 进程环境与进程属性
* linux 提供的进程环境以及进程属性
* 守护进程，日志信息，孤儿进程以及僵死进程

<!-- more -->


### 进程属性

用户可以利用 ps aux 命令查看当前用户系统所有进程相关信息

1. 进程号(PID):用 getpid() 获取

2. 父进程号(PPID): 用 getppid() 获取

3. 进程组号(PGID): 用 getpgrp() 获取

### 创建进程

利用 fork() 函数来创建，在父进程中创建成功将会返回子进程的 PID，子进程将返回 0，以示区别。

如下实例将父子进程执行的代码分开的实例，实际上父子进程都拥有这段代码，只不过根据条件选择执行与否。**fork() 函数创建子进程时，子进程是父进程的一份复制**

``` c
include<stdio.h>
include<sys/types.h>
include<unistd.h>

int main()
{
	pid_t pid;
	if(pid=fork()==-1)
	{
		printf("fork error");
	}
	else if(pid==0)
	{
		printf("int child process\n");
	}
	else
	{
		printf("in father process\n");
	}

	return 0;
}
```

另外创建子进程还可以通过 vfork(), fork() 是复制一个父进程的副本，从而拥有自己独立的代码段，数据段以及堆栈空间，即成为一个独立的实体。而 vfork() 是共享父进程的代码以及数据段。

### 回收进程用户空间资源

注册退出处理函数：

``` c
include<stdlib.h>

void test_exit(int status,void *arg)// os 回调
{
	printf("before exit()!\n");
	printf("exit %d\n",status);
	printf("arg=%s\n",(char *) arg);

}
int main()
{
	char *str="test";
	on_exit(test_exit,(void *)str);//只是 注册不是调用
	exit(4321);
}
```