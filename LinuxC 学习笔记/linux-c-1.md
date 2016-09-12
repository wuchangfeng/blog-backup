---
title: 利用管道实现双端通信
date: 2016-05-06 07:22:10
tags: linux-c
categories: About Linux
---

### 概念引入

有名管道可以依赖于文件系统，实现同主机任意进程之间的通信，它和普通文件一样具有磁盘存放路径，文件权限和其他属性，但是其在磁盘中并没真正的存储信息，而是在内存中，一旦两个进程结束自动丢失。

单向通信只需要一个管道即可，一个进程读，一个进程写。而双向通信，则需要两个，也就是我们引入的实例程序。

<!-- more -->


### 通信模型

如下图示建立两个通信管道，每个程序的父子进程**分别**做读写操作，为了能够实时的通信，可以加入 while 循环，阻塞程序，等待输入输出。

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E7%AE%A1%E9%81%93%E9%80%9A%E4%BF%A1.png)

### 实例程序

fifo_read.c

``` c
include<stdlib.h>
include<unistd.h>
include<stdio.h>
include<string.h>
include<fcntl.h>
include<limits.h>
include<sys/types.h>
include<sys/stat.h>
define FIFO_NAME1 "/tmp/fifo3"
define FIFO_NAME2 "/tmp/fifo4"

int main(int argc,char *argv)
{
	int pipe_fd;
	int res;
	char buffer[4096];
	int pid;
	int bytes_read=0;
	memset(buffer,'\0',sizeof(buffer));
		//unlink(FIFO_NAME1);
		mkfifo(FIFO_NAME1,0766);
		pid=fork();
		//child write fifo2
		if(pid==0)
		{
			while(1)
			{
				pipe_fd=open(FIFO_NAME2,O_WRONLY);
				if(pipe_fd >= 0)
				{
				scanf("%s",buffer);
				write(pipe_fd,buffer,sizeof(buffer));
				printf("\nwrite:");
				printf("%s",buffer);
				printf("\n");
				}
				else
				{
					printf("%d",pipe_fd);
				}
			}
		}
		// father read fifo1
		if(pid > 0)
		{
			while(1)
			{
				
				pipe_fd=open(FIFO_NAME1,O_RDONLY);
				if(pipe_fd >= 0)
				{
				read(pipe_fd,buffer,sizeof(buffer));
				printf("\nread:");
				printf("%s",buffer);
				printf("\n");
				}
				else
				{
					printf("error");
				}
			}
		}

			exit(EXIT_SUCCESS);
}
```

fifo_write.c

``` c
include<stdlib.h>
include<unistd.h>
include<stdio.h>
include<string.h>
include<fcntl.h>
include<limits.h>
include<sys/types.h>
include<sys/stat.h>
define FIFO_NAME1 "/tmp/fifo3"
define FIFO_NAME2 "/tmp/fifo4"
int main(int argc,char *argv)
{
	int pipe_fd;
	int res;
	char buffer[4096];
	int pid;
	int bytes_read=0;
	memset(buffer,'\0',sizeof(buffer));
		//unlink(FIFO_NAME2);
		mkfifo(FIFO_NAME2,0766);
		pid=fork();
		// child write fifo1
		if(pid==0)
		{
			while(1)
			{
				pipe_fd=open(FIFO_NAME1,O_WRONLY);
				if(pipe_fd >= 0)
				{
				scanf("%s",buffer);
				write(pipe_fd,buffer,sizeof(buffer));
				printf("\nwrite:");
				printf("%s",buffer);	
				printf("\n");
				}
				else
				{
					printf("error");
				}			
			}
		}
		// father read fifo2
		if(pid > 0)
		{
				while(1)
				{
					pipe_fd=open(FIFO_NAME2,O_RDONLY);
					if(pipe_fd >=0)
					{
					bytes_read=read(pipe_fd,buffer,sizeof(buffer));
					printf("\nread:");
					printf("%s",buffer);
					printf("\n");
					}
					else
					{
						printf("error");
					}
				}
		}
		exit(EXIT_SUCCESS);
}
```