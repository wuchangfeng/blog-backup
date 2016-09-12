---
title: linux 异步信号处理机制
date: 2016-05-06 07:44:35
tags: linux-c
categories: About Linux
---

* 一个进程向另一个进程发送信号，也可以包括进程本身
* 安装信号，即如何设置信号到来时的处理方式
* 介绍如何屏蔽信号以及信号集合
* 介绍等待信号，即阻塞当前进程直到某些信号到来后继续执行相应的操作

<!-- more -->

### 引入

信号可以导致一个正在运行的进程被异步打断，转而处理突发事件。

### 发送信号

* kill 发送一个信号到进程

* raise 自举一个信号

* ualarm 定时

``` c
include<unistd.h>
include<signal.h>
include<errno.h>
include<stdio.h>

void handler()
{
	printf("int:hello\n");
}

int main()
{
	int i;
	signal(SIGALRM,handler);
	printf("%d\n",ualarm(50,20));
	//do what?
	while(1)
	{
		sleep(1);
		printf("test\n");
	}

}

```

### 安装和捕获信号