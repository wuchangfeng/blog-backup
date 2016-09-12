---
title: System V 进程间通信之消息队列
date: 2016-05-13 10:44:48
tags: linux-c
categories: About Linux
---

System V 进程间通信机制，分别是消息队列，信号量，共享内存。这三种通信方式用于同一主机的进程间的消息同步或者传递。

Linux 系统为每个 IPC 通信对象都唯一分配了 ID，所有针对 IPC 的操作都使用此 ID，即通信双方需要获取此 ID，此问题解决办法是使用约定的 key 来解决即 ftok。

这一篇博客讲的是消息队列通信机制，其主要用来实现两个进程间少量的数据传输，并且接收方可以根据消息队列中消息的类型选择性的接受消息。消息队列是一个链式队列。为什么消息队列采用单链表来存储消息数据？

<!-- more -->

### 一 . 消息队列

消息队列提供了一种从一个进程向另一个进程发送一个数据块的方法。 每个数据块都被认为含有一个类型，接收进程可以独立地接收含有不同类型的数据结构。我们可以通过发送消息来避免命名管道的同步和阻塞问题。但是消息队列与命名管道一样，每个数据块都有一个最大长度的限制。

### 二 . 消息队列相关定义

**msgget函数**

创建消息队列：

	int msgget(key_t, key, int msgflg);

其中 key 由 ftok 创建，而 msgflg 为消息队列的访问权限

**msgctl**

消息队列属性控制
	
	int msgctl(int msgid, int command, struct msgid_ds *buf);

msgid 为消息队列标识符，其为使用 msgget 函数创建消息队列的返回值

command 为执行的控制命令，即要执行的操作，可取值如下：

IPC_STAT：把msgid_ds结构中的数据设置为消息队列的当前关联值，即用消息队列的当前关联值覆盖msgid_ds的值。

IPC_SET：如果进程有足够的权限，就把消息列队的当前关联值设置为msgid_ds结构中给出的值

IPC_RMID：删除消息队列

**msgsend函数**

发送消息到消息队列：
	
	int msgsend(int msgid, const void *msg_ptr, size_t msg_sz, int msgflg);

msgid是由msgget函数返回的消息队列标识符。

msg_ptr是一个指向准备发送消息的指针，但是消息的数据结构却有一定的要求，指针msg_ptr所指向的消息结构一定要是以一个长整型成员变量开始的结构体，接收函数将用这个成员来确定消息的类型。所以消息结构要定义成这样：

	struct msgbuf{  
    	long mtype;
		char text[1];  
	};
  
msg_sz是msg_ptr指向的消息的长度，注意是消息的长度，而不是整个结构体的长度，也就是说msg_sz是不包括长整型消息类型成员变量的长度。

msgflg用于控制当前消息队列满或队列消息到达系统范围的限制时将要发生的事情。

如果调用成功，消息数据的一分副本将被放到消息队列中，并返回0，失败时返回-1。

**msgrcv函数**

从消息队列接收消息，注意其第四个参数类型为 long：

	int msgrcv(int msgid, void *msg_ptr, size_t msg_st, long int msgtype, int msgf)

msgtype可以实现一种简单的接收优先级。如果msgtype为0，就获取队列中的第一个消息。如果它的值大于零，将获取具有相同消息类型的第一个信息。如果它小于零，就获取类型等于或小于msgtype的绝对值的第一个消息。

msgflg用于控制当队列中没有相应类型的消息可以接收时将发生的事情。

调用成功时，该函数返回放到接收缓存区中的字节数。


### 三 . 消息队列应用实例

``` c
#include<stdio.h>
#include<unistd.h>
#include<stdlib.h>
#include<sys/types.h>
#include<sys/ipc.h>
#include<string.h>
#include<sys/msg.h>
#include<sys/stat.h>
struct msg_buf
{
	long type;// type of message
	char msg[128];// message text
};

int main(int argc,char *argv[])
{
	key_t key;
	int msgid;
	struct msg_buf msg_snd, msg_rcv;
	struct msqid_ds buf;
	// what you write
	char *ptr="helloworld\n";
	memset(&msg_snd,'\0',sizeof(struct msg_buf));
	memset(&msg_rcv,'\0',sizeof(struct msg_buf));
	msg_rcv.type=1;
	msg_snd.type=1;
	memcpy(msg_snd.msg,ptr,strlen(ptr));
	key=ftok(".",'A');
	// build 
	msgid=msgget(key,0600|IPC_CREAT);
	// send message
	printf("msg_snd_return=%d\n",msgsnd(msgid,&msg_snd,strlen(msg_snd.msg),0));
	// get message
	msgrcv(msgid,&msg_rcv,128,msg_rcv.type,0);
	// output message
	printf("%s",msg_rcv.msg);
}
```

### 四 . 参考实例程序

[linux 进程间通信](http://blog.csdn.net/liu5320102/article/details/50848266)


发送端的程序

``` c
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/msg.h>
#include <errno.h>

#define MAX_TEXT 512
struct msg_st
{
	long int msg_type;
	char text[MAX_TEXT];
};

int main()
{
	int running = 1;
	struct msg_st data;
	char buffer[BUFSIZ];
	int msgid = -1;

	//建立消息队列
	msgid = msgget((key_t)1234, 0666 | IPC_CREAT);
	if(msgid == -1)
	{
		fprintf(stderr, "msgget failed with error: %d\n", errno);
		exit(EXIT_FAILURE);
	}

	//向消息队列中写消息，直到写入end
//	while(running)
//	{
		//输入数据
		printf("Enter some text: ");
		fgets(buffer, BUFSIZ, stdin);
		data.msg_type = 1;    //注意2
		strcpy(data.text, buffer);
		//向队列发送数据
		if(msgsnd(msgid, (void*)&data, MAX_TEXT, 0) == -1)
		{
			fprintf(stderr, "msgsnd failed\n");
			exit(EXIT_FAILURE);
		}
		//输入end结束输入
		if(strncmp(buffer, "end", 3) == 0)
			running = 0;
		sleep(1);
//	}
	exit(EXIT_SUCCESS);
}

```

接受端的程序

``` c
//http://blog.csdn.net/ljianhui/article/details/10287879
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/msg.h>

struct msg_st
{
	long int msg_type;
	char text[BUFSIZ];
};

int main()
{
	int running = 1;
	int msgid = -1;
	struct msg_st data;
	long int msgtype = 0; //注意1

	//建立消息队列
	msgid = msgget((key_t)1234, 0666 | IPC_CREAT);
	if(msgid == -1)
	{
		fprintf(stderr, "msgget failed with error: %d\n", errno);
		exit(EXIT_FAILURE);
	}
	//从队列中获取消息，直到遇到end消息为止
	//while(running)
	//{
		if(msgrcv(msgid, (void*)&data, BUFSIZ, msgtype, 0) == -1)
		{
			fprintf(stderr, "msgrcv failed with errno: %d\n", errno);
			exit(EXIT_FAILURE);
		}
		printf("You wrote: %s\n",data.text);
		//遇到end结束
		if(strncmp(data.text, "end", 3) == 0)
			running = 0;
	//}
	//删除消息队列
	if(msgctl(msgid, IPC_RMID, 0) == -1)
	{
		fprintf(stderr, "msgctl(IPC_RMID) failed\n");
		exit(EXIT_FAILURE);
	}
	exit(EXIT_SUCCESS);
}
```