---
title: 匿名管道以及重定向编程
date: 2016-05-08 13:05:42
tags: linux-c
categories: About Linux
---

大部分学校都有校内 OJ 用来给学生刷题，老师上课说了一下它的实现原理，记录下来顺便学习一下匿名管道以及重定向编程。

OJ 判分最大的特点就是：评分人不会在电脑面前等着你写好，然后给你逐行分析，测试。只用根据自己输入的数，就能给你打分。

<!-- more --> 

### 一 . 无名管道概念

默认情况下，一个进程打开三个设备文件：标准输入(键盘),标准输出(显示器),标准错误输出。无名管道的内核资源在通信两端进程结束后，会自动结束，不会保留。

1. 创建无名管道

	extern int pipe (int _pipedes[2])

其中 _pipedes[0] 用来完成读操作，_pipedes[1] 用来完成写做操

2. 读写无名管道

### 二 . 文件描述符重定向

重定向操作对保存程序的输出结果又很大的帮助。

1. shell 重定向基本操作

	cat<test01

上述程序执行成功的标准是 test01 文件的存在，作用就是将 test01 文件的内容作为输入信息(可以理解其作为屏幕的输入信息)

2. 重定向编程

普通的输出函数(printf()),默认将某信息写入到文件描述符为1的文件中(输出)，普通输入函数都默认从文件描述符为0的文件中读取数据。因此重定向编程实际上是关闭某个标准输入输出设备(0,1,2),而将另一个普通文件的文件描述符设置为 0,1,2

着重看一下 dup2(),其声明如下：
	
	extern int dup2(int _fd,int _fd2)

如果 fd2 为已打开，则关闭，fd 与 fd2 具有很多共性

### 三 . 重定向编程实例

该实例的作用：开发者编写 test2.c 程序，实现预定功能(本实例实现加法运算)，测试者只需要在 test1.c 中输入给定数目，即可判断 test.2 是否正确。当然后台的实际操作者都是 work.c。


work.c

``` c
include<stdio.h>
include<sys/types.h>
include<unistd.h>
include<fcntl.h>
include<sys/wait.h>
include<stdlib.h>

int main(int args,char *argv[])
{
	int fds[2];
	// build pipe
	pipe(fds);
	
	// build child process and redirct to the pipe for input
	if(fork() == 0)
	{
		char buff[128];
		dup2(fds[0],0);
		close(fds[1]);
		execlp("./test2","test2",(char *)0);	
	}
	else
	{
		// build another child process and redirct to the pipe for output
		if(fork() == 0)
		{
			dup2(fds[1],1);
			close(fds[0]);
			execlp("./test1","test1",(char *)0);		
			
                 }
		else
		{
			close(fds[0]);
			close(fds[1]);
			wait(NULL);
			wait(NULL);
		}
	}
		
	return 0;
}
```
test2.c

``` c
#include<stdio.h>

int main(void)
{
	int m,n;
	scanf("%d%d",&m,&n);
	printf("%d\n",m+n);


	return 0;
}
```
test1.c

``` c
#include<stdio.h>

int main(void)
{
	printf("%d %d",5,6);
	return 0;
}
```