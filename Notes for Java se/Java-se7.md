---
title: Java IO 流
date: 2015-12-17 14:52:03
tags: java
categories: About Java
---

读书笔记之 java 文件IO流(undone)

<!-- more -->

### 1. File 类


### 2. 文件过滤器


### 3. Java IO流

其实应该首先问自己一句为什么需要IO流，有什么好处，解决了什么问题？

* 字节流和字符流

用法几乎完全一样，而字节流操作的数据单元是8位字节，字符流操作的数据单位是16位字符。

字节流由 InputStream 和 OutputStream 作为基类，字符流由 Reader 和 Writer 作为基类。

* 节点流和处理流

可以从/向一个**特定的IO设备**(磁盘，网络)读/写数据的流，称为节点流。处理流就是用来包装节点流的，一种典型的装饰者模式，包装之后，可以消除不同节点流之间的区别。

* 流的模型概念

 * InputStream/Reader:所有输入流的基类，前者是字节输入流，后者是**字符输入流**。
 * OutputStream/Writer:所有输出流的基类，前者字节输出流，后者字符输出流。

### 4. 字节流 和 字符流 

InputStream 和 Stream 都是抽象类，它们分别有一个用于读取文件的输入流：FileInputStream,FileReader,它们都是节点流-会直接和指定文件关联。一个FileInputStream实例如下：

		public class FileInputStreamTest {
          public static void main(String[] args) throws IOException {
        //创建字节输入流
        FileInputStream fis = new FileInputStream("G:\\HtmlParser.py");
        //创建一个长度为 1024 的 "竹筒"
        byte[] buff = new byte[1024];
        //用于保存实际读取的字节数
        int hasRead = 0;
        //使用循环来重复"取水过程"
        while( (hasRead = fis.read(buff)) > 0){

            //取出字节转换成字符串输出
            System.out.println(new String(buff,0,hasRead));
        }
        // 关闭文件输入流
        fis.close();
	    }
	}


一个 FileReader 实例如下：

``` java
	public class FileReaderTest {

    public static void main(String[] args)
            throws IOException{
        //创建字符输入流
        FileReader fr = new FileReader("ttt.txt");
        //创建一个长度为 32 的 "竹筒"
        char[] cbuf = new char[32];
        //用来保存读取的字符数目
        int hasRead = 0;
        while ((hasRead = fr.read(cbuf)) > 0){
            //取出竹筒中的水滴，字符数组转换成字符串输入
            System.out.println(new String(cbuf,0,hasRead));
    	    }
    	}
	}
```


输出流也是一样的道理昂。



### 5. 处理流

处理流就是来包装节点流的昂，这样处理起来更加方便啊，什么样的流是处理流？只要流的构造器参数是一个物理节点。如下用 PrintStream 来包装 OutputStream。

	FileOutputStream fos = new FileOutputStream("test.txt");
	PrintStream ps = new PrintStream(fos);


### 6. 转化流

字节流--》 字符流，看一个示例就明白了。
    
	```java

	public class KeyinTest {

    public static void main(String[] args) throws
            IOException{
        //字节输入流转化成字符输入流
        InputStreamReader reader = new InputStreamReader(System.in);
        //reader 包装为 BufferedReader  其具有缓冲功能，一次可读取一行
        //换行为结束标志，没有换，则程序阻塞，等待。
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        while((line = br.readLine())!=null){
            if (line.equals("a")){
                System.exit(1);
            }
            System.out.println("输入的内容为：" + line);
    	    }
    	}
	}

	```


### 7. 重新定向标准输入/输出

### 8 .Java 虚拟机读写其他进程的数据

### 9. RandomAccessFile



### 10. 对象序列化

#### 概念

即 java 对象 --> 与平台无关的二进制流，so can store or pass。

为了让某个类是可序列化的，必须实现以下：

* Serializable
* Externalizable



#### 使用对象流实现序列化

某个类实现了 Serializable ，则其对象可序列化。关键步骤如下：

	ObjectOutputStream oos = new ObjectOutputStream(
		new FileOutputStream("object.txt"));
	oos.writeObject(per);

如要从二进制流中恢复数据，称为反序列化。需要将对象强转至真实类型。
	
	//从输入流中读取一个java对象，并将其强制类型转换为Person类
	Person p = (Person)ois.readObject();

上面读取的仅仅是 java 对象的数据，不是类。**反序列化机制无需通过构造器来初始化 java 对象**，后面说这样就可以利用 java 的反序列化机制来克隆对象啦==


#### 对象引用的序列化

某个类的成员变量是引用类型的，那么该引用类必须是可序列化的，否则持有该引用变量的对象也不是可序列化的。

java 序列化机制采用了一种特殊的序列化算法，内容如下：

* 所有保存到磁盘中的对象都有一个序列化编号。
* 当程序试图序列化一个对象时，先试图检查该对象是否被序列化过，只有对象从未被序列化过，系统才会将该对象转换成自己序列并输出。
* 如果某个对象已经序列化过，程序将直接输出一个序列化编号，不会再次序列化该对象。

由此可见 java 序列化机制，如果多次序列化 同一个 java 对象，只有第一次才会把该 java 对象转换成字节序列并输出。 也会引起问题。。。




#### 自定义序列化

* 使用 **transient**来避免某个实例变量被序列化
* 虽然上面这个方法简单，但是这样会完全将 transient 修饰的关键字隔离在序列化机制外。java 也提供了一种自定义的序列化机制，个人感觉就是虽然用 transient 修饰了实例变量，但是不代表不可以序列化它，反而可以自定义，在后面的源码分析经常可以看到，重写了 writeObject().



### 11. NIO

BufferedReader 如果没有读取到期待的流，将会阻塞该线程的执行，效率不高。 jdk 1.4 改进啊==

#### java NIO 

老IO 和 新IO 都是用来输入输出，新IO采用内存映射文件的方式来处理输入/输出，新 IO 将文件或者文件的一段区域映射到内存中，这样**就可以像访问内存一样访问文件了**

Channel 和 Buffer 是核心，Channel 是对传统IO的模拟，在新IO中所有数据都需要通过通道传输；Channel 与传统的 InputStream，outPutStream，最大的区别是提供了一个 map(),其可以直接将数据映射到内存中。

Buffer 理解为一个容器，本质是一个数组，发送到 Channel 中所有对象都必须首先送到 Buffer 中，而从 Channel 中读数据也必须首先放到 Buffer 中。

#### Buffer

#### Channel


#### 字符集和 CharSet


### 12. 文件锁

用于多个进程并发修改同一个文件，在 FileChannel 提供了 lock()/tryLock() 方法可获得 FileLock 对象。

* lock():当其试图锁定一个文件对象时，如果没有无法获得该文件对象，程序阻塞。
* tryLock():能够锁，就返回该文件锁，不能返回就返回null

当然也提供了一些方法，来锁定部分文件对象。

文件锁好像没有什么卵用啊==

### 13. NIO.2