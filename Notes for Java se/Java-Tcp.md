---
title: 基于 TCP 协议的网络编程
toc: true
date: 2016-05-15 21:25:20
tags: java
categories: About Java
description:
feature:
---

* TCP 协议以及 SOCKET 编程入门
* 三次握手以及四次挥手
* 长连接，短连接以及 Keep-alive 功能

<!--more-->

## 一 . TCP 简介

* TCP 是基于 端到端的协议，TCP 可以让两台计算机建立一个连接：用于发送和接受信息的**虚拟链路**

* TCP 协议负责收集这些信息包，按序传送以及接收时正确还原信息
* TCP 协议具备重发机制，当为收到接收端的确认信息时，发送端会继续重发，保证了通信的可靠性
* TCP/IP 通常互相结合来使用，即互补
* TCP 的建立需要经历三次握手，四次挥手

## 二 . TCP 三次握手以及四次挥手过程

**三次握手**

1. 第一次握手：建立连接时，客户端发送 syn 包（syn=j）到服务器，并进入 SYN_SENT 状态，等待服务器确认；SYN：同步序列编号（Synchronize Sequence Numbers）。

2. 第二次握手：服务器收到 syn 包，必须确认客户的 SYN（ack=j+1），同时自己也发送一个SYN包（syn=k），即 SYN+ACK 包，此时服务器进入 SYN_RECV 状态；

3. 第三次握手：客户端收到服务器的 SYN+ACK包，向服务器发送确认包ACK(ack=k+1），此包发送完毕，客户端和服务器进入 ESTABLISHED（TCP连接成功）状态，完成三次握手。

**四次挥手**

1. 第一步，当主机 A 的应用程序通知 TCP 数据已经发送完毕时，TCP 向主机 B 发送一个带有 FIN 附加标记的报文段（FIN表示英文finish）。

2. 第二步，主机 B 收到这个 FIN 报文段之后，并不立即用 FIN 报文段回复主机 A，而是先向主机A发送一个确认序号 ACK，同时通知自己相应的应用程序：对方要求关闭连接（先发送ACK的目的是为了防止在这段时间内，对方重传FIN报文段）。

3. 第三步，主机 B 的应用程序告诉 TCP：我要彻底的关闭连接，TCP 向主机 A 送一个 FIN 报文段。

4. 第四步，主机 A 收到这个 FIN 报文段后，向主机 B 发送一个 ACK 表示连接彻底释放。


## 三 . TCP 长连接以及短连接

**TCP短连接**

我们模拟一下 TCP 短连接的情况，client 向 server 发起连接请求，server 接到请求，然后双方建立连接。client 向 server 发送消息，server 回应 client，然后一次读写就完成了，这时候双方任何一个都可以发起 close 操作，不过一般都是 client 先发起close操作。为什么呢，一般的server不会回复完client后立即关闭连接的，当然不排除有特殊的情况。从上面的描述看，短连接一般只会在client/server间传递一次读写操作

短连接的优点是：管理起来比较简单，存在的连接都是有用的连接，不需要额外的控制手段

**TCP长连接**

接下来我们再模拟一下长连接的情况，client向server发起连接，server接受client连接，双方建立连接。Client与server完成一次读写之后，它们之间的连接并不会主动关闭，后续的读写操作会继续使用这个连接。

首先说一下TCP/IP详解上讲到的 **TCP保活功能**，保活功能主要为服务器应用提供，服务器应用希望知道**客户主机是否崩溃**，从而可以代表客户使用资源。如果客户已经消失，使得服务器上保留一个半开放的连接，而服务器又在等待来自客户端的数据，则服务器将应远等待客户端的数据，保活功能就是试图在服务器端检测到这种半开放的连接。

如果一个给定的连接在两小时内没有任何的动作，则服务器就向客户发一个探测报文段，客户主机必须处于以下4个状态之一：

1. 客户主机依然正常运行，并从服务器可达。客户的TCP响应正常，而服务器也知道对方是正常的，服务器在两小时后将保活定时器复位。
2. 客户主机已经崩溃，并且关闭或者正在重新启动。在任何一种情况下，客户的TCP都没有响应。服务端将不能收到对探测的响应，并在75秒后超时。服务器总共发送10个这样的探测 ，每个间隔75秒。如果服务器没有收到一个响应，它就认为客户主机已经关闭并终止连接。
3. 客户主机崩溃并已经重新启动。服务器将收到一个对其保活探测的响应，这个响应是一个复位，使得服务器终止这个连接。
4. 客户机正常运行，但是服务器不可达，这种情况与2类似，TCP能发现的就是没有收到探查的响应。

从上面可以看出，TCP**保活功能(keep-alive)**主要为探测**长连接**的存活状况，不过这里存在一个问题，存活功能的探测周期太长，还有就是它只是探测TCP连接的存活，属于比较斯文的做法，遇到恶意的连接时，保活功能就不够使了。


## 四 . SOCKET 编程(入门)

下面是基于 TCP 协议的 SOCKET 编程

服务端的程序

``` java
import java.net.*;
import java.io.*;
public class Server
{
	public static void main(String[] args) 
		throws IOException
	{
		// 创建一个ServerSocket，用于监听客户端Socket的连接请求
		ServerSocket ss = new ServerSocket(30000);
		// 采用循环不断接受来自客户端的请求
		while (true)
		{
			// 每当接受到客户端Socket的请求，服务器端也对应产生一个Socket
			Socket s = ss.accept();
			// 将Socket对应的输出流包装成PrintStream
			PrintStream ps = new PrintStream(s.getOutputStream());
			// 进行普通IO操作
			ps.println("您好，您收到了服务器的新年祝福！");
			// 关闭输出流，关闭Socket
			ps.close();
			s.close();
		}
	}
}
```

* Socket accept():如果接收到一个客户端的 socket ，则该方法返回一个与客户端对应的 socket，可见每个 tcp 连接有两个 Socket；否则该方法一直处于等待状态，也就是阻塞状态。
* ServerSocket(int port):指定端口 0-65535。构造函数还有不同的参数，注意。
* while 循环，接受来自不同客户端的请求。
* ServerSocket() 默认 IP 地址为本机。

客户端程序

``` java
import java.net.*;
import java.io.*;
public class Client
{
	public static void main(String[] args) 
		throws IOException
	{
		Socket socket = new Socket("127.0.0.1" , 30000); 
		// 将Socket对应的输入流包装成BufferedReader
		BufferedReader br = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
		// 进行普通IO操作
		String line = br.readLine();
		System.out.println("来自服务器的数据：" + line);
		// 关闭输入流、socket
		br.close();
		socket.close();
	}
}
```

* s.setSoToTimeout(10000):设置连接超时时长

## 五 . SOCKET 编程(多线程)

考虑到客户端与服务器进行通信，双方都会有阻塞的状态。所以服务器应该为每个 SOCKET 单独启动一个线程，每个线程负责与一个客户端通信。

创建 ServerSocket 监听的主类

``` java

import java.net.*;
import java.io.*;
import java.util.*;

public class MyServer
{
	//定义保存所有Socket的ArrayList
	public static ArrayList<Socket> socketList
		= new ArrayList<>();
	public static void main(String[] args) 
		throws IOException
	{
		ServerSocket ss = new ServerSocket(30000);
		while(true)
		{
			// 此行代码会阻塞，将一直等待别人的连接
			Socket s = ss.accept();
			socketList.add(s);
			// 每当客户端连接后启动一条ServerThread线程为该客户端服务
			new Thread(new ServerThread(s)).start();
		}
	}
}
```

服务端的线程类

``` java

import java.io.*;
import java.net.*;

// 负责处理每个线程通信的线程类
public class ServerThread implements Runnable 
{
	// 定义当前线程所处理的Socket
	Socket s = null;
	// 该线程所处理的Socket所对应的输入流
	BufferedReader br = null;
	public ServerThread(Socket s)
	throws IOException
	{
		this.s = s;
		// 初始化该Socket对应的输入流
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	public void run()
	{
		try
		{
			String content = null;
			// 采用循环不断从Socket中读取客户端发送过来的数据
			while ((content = readFromClient()) != null)
			{
				// 遍历socketList中的每个Socket，
				// 将读到的内容向每个Socket发送一次
				for (Socket s : MyServer.socketList)
				{
					PrintStream ps = new PrintStream(s.getOutputStream());
					ps.println(content);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	// 定义读取客户端数据的方法
	private String readFromClient()
	{
		try
		{
			return br.readLine();
		}
		// 如果捕捉到异常，表明该Socket对应的客户端已经关闭
		catch (IOException e)
		{
			// 删除该Socket。
			MyServer.socketList.remove(s);      // ①
		}
		return null;
	}
}
```

## 四 . 半关闭的 SOCKET

需要面临解决的问题：Socket 的输出流如何表示数据的输出已经结束？网络通信中不能通过关闭输出流来表示数据的输出已经结束，因为关闭输出流时，对应的 Socket 也关闭了，这样就不能通过该 Socket 来读取数据了。

这种情况下，Socket 提供了两个半关闭的方法：

* shutdownInput():关闭该 Socket 的输入流，程序还可以通过该 Socket 的输出流输出数据。
* shutdownOutput():关闭该 Socket 的输出流，程序还可以通过该 Socket 的输入流输入数据。

Socket 在这两个方法的作用下，并没有关闭，只是不能读写而已。


``` java

import java.io.*;
import java.net.*;
import java.util.*;
// 服务端
public class Server
{
	public static void main(String[] args) 
		throws Exception
	{
		ServerSocket ss = new ServerSocket(30000);
		Socket socket = ss.accept();
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println("服务器的第一行数据");
		ps.println("服务器的第二行数据");
		// 关闭socket的输出流，表明输出数据已经结束
		socket.shutdownOutput();
		// 下面语句将输出false，表明socket还未关闭。
		System.out.println(socket.isClosed());
		// 继续读取数据
		Scanner scan = new Scanner(socket.getInputStream());
		while (scan.hasNextLine())
		{
			System.out.println(scan.nextLine());
		}
		scan.close();
		socket.close();
		ss.close();
	}
}
```

这种通信方式，不适合于保持持久通信状态的交互式应用，只适用于一站式通信协议。