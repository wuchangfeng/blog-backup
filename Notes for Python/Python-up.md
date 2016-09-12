---
title: Python-进阶学习
date: 2016-03-14 15:18:34
tags: Python
categories: About Python
---

Python 进阶学习，包含Python的重难点以及易忽略的地方用的少的知识点。

<!-- more -->
### 0.0 序列化
把变量从内存中变成可存储或传输的过程称为序列化，在python中叫做pickling,在其他语言中称为serialization，marshalling，flattening。

序列化之后，就可以把序列化后的内容写入磁盘，或者通过网络传输到别的机器上。

反过来，把变量内容从序列化的对象重新读到内存里称之为反序列化，即unpickling。



### 0.1 文件操作相关
    import os
    # 改变当前工作目录至指定目录
	os.chdir()
	# 查看当前目录的绝对路径:
	>>> os.path.abspath('.')
	'/Users/michael'
	# 在某个目录下创建一个新目录，首先把新目录的完整路径表示出来:
	>>> os.path.join('/Users/michael', 'testdir')
	'/Users/michael/testdir'
	# 然后创建一个目录:
	>>> os.mkdir('/Users/michael/testdir')
	# 删掉一个目录:
	>>> os.rmdir('/Users/michael/testdir')

    # 对文件重命名:
	>>> os.rename('test.txt', 'test.py')
	# 删掉文件:
	>>> os.remove('test.py')

利用Python的特性来过滤文件

	>>> [x for x in os.listdir('.') if os.path.isdir(x)]
    ['.lein', '.local', '.m2', '.npm', '.ssh', '.Trash', '.vim', 'Applications', 'Desktop', ...]


### 1.迭代器

迭代器代表一个数据流对象，不断重复调用迭代器的next()方法可以逐次地返回数据流中的每一项，当没有更多数据可用时，next()方法会抛出异常StopIteration。此时迭代器对象已经枯竭了，之后调用next()方法都会抛出异常StopIteration。迭代器需要有一个__iter()__方法用来返回迭代器本身。因此它也是一个可迭代的对象。

廖老师一句话，Python 中的迭代器的抽象程度要高于 java 等，因为 Python 中的**迭代对象**的范围更广，不仅可以在 list 或者 tuple 上，也可以在 dict，字符串等对象上。

另外 Python也是由办法可以判断一个对象是否是可迭代的。呵呵。

最后，如果要像 java 中那样，输出索引下表怎么办？Python内置的**enumerate函数**可以把一个list变成索引-元素对。代码如下：

	>>> for i, value in enumerate(['A', 'B', 'C']):
	...     print(i, value)

	0 A
	1 B
	2 c


### 1.生成器

通过列表生成式，我们可以很简单的直接创建一个列表，但是受到内存限制，列表容量有限。所以我们有这种办法：列表后面的元素可以按照某种算法推算出来。可以节省空间。



1.1 创建generator(生成器)很简单，只需把列表生成式的[]改成()即可。

	>>> L = [x*x for x in range(10)]
	>>> L
	[0,1,4,9,16,25,36,49,64,81]
	>>> g = (x*x for x in range(10))
	>>> g
	<generator object <genexpr> at 0x104feab40>

通过generator的next()方法可以一个一个的打印出其每一个元素。其中注意generator保存的是算法。

上面这种不断调用next()方法实在是太变态了，正确的方法是使用for循环，因为generator也是可迭代对象。

	>>> g = (x*x for x in range(10))
	>>> for n in g:
	        print n

1.2 如果一个函数定义中包含yield关键字，那么这个**函数**就不再是一个普通函数，而是一个generator。
普通的函数执行是顺序执行，遇到 return 语句或者最后一个执行语句就返回。而变成generator函数，在每次调用 next() 时候执行，遇到yield 语句返回，再次执行时，从上次返回的 yield 语句处继续执行。

	>>> def odd():
	   print'step 1'
	   yield 1
       print 'step2'
	   yield 3
       print 'step3'
       yield 5
	>>> o = odd()
	>>> o.next()
	step 1
    1
	
	>>> o.next()
	step 2
    3
    
    >>> o.next()
    step 3
    5
总结：生成器是一个非常有用的工具，它是在for循环的过程中不断计算出下一个元素，并在适当的条件结束for循环。对于函数改成的generator来说，遇到return语句或者执行到函数体最后一行语句，就是结束generator的指令，for循环随之结束。普通的函数改造成生成器之后，可以认为其是一个**迭代器**。不知道这样说对不对？**注意迭代器是函数，不是什么 yield 关键字**

最后生成器只能迭代一次，第一次能正确输出结果，后面就不行了，因为它**并没有在内存中保存**，如下图：

![](http://7xrl8j.com1.z0.glb.clouddn.com/generator.jpg)


### 2.返回函数

2.1 函数作为返回值

通常情况下，我们实现一个可变参数的求和，是如下定义：

	def calac_sum(*args):
	  ax = 0
	  for n in args:
      	ax = ax +n
      return ax

但是如果不需要立刻求和，而是在后面的代码中，根据需要再计算怎么办？可以不返回求和的结果，而返回求和的函数。

	def lazy_sum(*args):
	    def sum():
            ax = 0
            for n in args:
                ax = ax +n
            return ax
        return sum

当我们调用lazy_sum()时，返回的并不是求和结果，而是求和函数：
	>>> f = lazy_sum(1,3,5,7,9)
	>>> f
	<function sum at 0x10452f668>

调用函数f时，才返回正真计算的求和结果：
	>>> f()
	25

上面的例子中，我们在函数lazy_sum中定义了函数sum，并且内部函数sum可以引用外部函数lazy_sum的参数和局部变量，当lazy_sum返回函数sum时，相关参数和变量都保存在返回的函数中，这种称为：闭包 的程序结构拥有极大的威力。并且每次调用lazy_sum()时，都会返回一个新的函数，即使传入相同的参数。

2.2 闭包

注意到返回的函数在其定义内部引用了局部变量args，所以，当一个函数返回了一个函数后，其内部的局部变量还被新函数引用，所以，闭包用起来简单，实现起来可不容易。

另外一个需要注意的问题是，返回的函数并没有立刻执行，而是调用了f()才执行。如下：

	def count():
		fs = []
        for i in range(1,4):
			def f():
				return i * i
			fs.append(f)
		return fs
	
	f1,f2,f3 = count()

这个例子，每次循环都创建了一个新的函数，然后，把创建的三个函数都返回了。一般以为返回的结果1，4，9.但是实际结果都是9

**能够想出来为什么吗？**

原因就在于返回的函数引用了变量i，但它并非立刻执行。等到3个函数都返回时，它们所引用的变量i已经变成了3，因此最终结果为9。
返回闭包时牢记的一点就是：返回函数不要引用任何循环变量，或者后续会发生变化的变量。

而如果一定要引用循环变量怎么办？方法是再创建一个函数，用该函数的参数绑定循环变量当前的值，无论循环变量后续如何更改，已绑定到函数参数的值不变。

	def count():
		fs = []
		for i in range(1,4):
			def f(j):
				def g():
					return j*j
				return g
			fs.append(f(i))
		return fs

	>>> f1,f2,f3 = count()
	>>> f1()
	1
    >>> f2()
    4
	>>> f3()
	9

lmabda 的写法 

	f1, f2, f3 = [(lambda i = i : i * i) for i in range(1, 4)]

### 3.匿名函数

匿名函数很简单，只有一个表达式：
	>>> f = lambda x: x*x
	>>> f
	<function <lambda> at 0x101c6ef28>
	>>> f(5)
	25

### 4.装饰器(后面比较难理解)

由于函数也是一个对象，而且函数对象可以被赋值给变量，所以，通过变量也能调用该函数。

	>>>def now():
	       print '2011'
    >>> f = now
    >>> f()
    2011

而，我们需要增加now()函数的功能，比如，在函数调用前后自动打印日志，但是不希望修改now()函数的定义，这种**在代码运行期间动态增加功能的方式**，我们称为**装饰器**（Decorator）

廖雪峰大大说：decorator就是一个返回函数的高阶函数。不是太能理解。
所以，我们要定义一个能打印日志的decorator，可以如下定义：

	>>>def log(func):
		   def wrapper(*args,**kw):
     		   print 'call %s():' %func._name_
    		   return func(*args,**kw)
       return wrapper

如上Log，因为他是一个decorator，所以接受一个函数作为参数，并且返回一个函数。

	@log
    def now():
        print '2013-12-25'

调用now()函数，不仅会运行now()函数本身，还会在运行now()函数前打印一行日志。

	>>> now()
	call now():
    2013-12-25

如果decorator也就是下面的额log()函数本身需要传入参数,那就需要编写一个返回decorator的高阶函数。

	def log(text):
    def decorator(func):
        def wrapper(*args, **kw):
            print '%s %s():' % (text, func.__name__)
            return func(*args, **kw)
        return wrapper
    return decorator

用法如下

	@log('execute')
	def now():
		print '2013-12-25'

[Python 装饰器与闭包详解](http://www.oschina.net/translate/decorators-and-functional-python)

### 5.偏函数

偏函数的定义很简单 functools.partial 即有一个默认参数，但是传入新的参数之后，自动覆盖。


### 6.封装

本身很简单，在Python中再细化一下，实例本来就拥有的数据什么的，没必要在外部在定义方法去获取，直接在函数内部定义访问数据的函数，这样实例就可以直接调用这些函数，而不必知道函数内部的实现细节。

和静态语言不同，Python允许对实例变量绑定任何数据，也就是说，对于两个实例变量，虽然它们都是同一个类的不同实例，但拥有的变量名称都可能不同

如果某个内部属性不想被外部函数访问，可以把属性的名称前加上两个下划线__，在Python中，实例的变量名如果以__开头，就变成了一个私有变量（private），只有内部可以访问，外部不能访问。

	class Student(object):

    def __init__(self, name, score):
        self.__name = name
        self.__score = score

    def print_score(self):
        print '%s: %s' % (self.__name, self.__score)

外部变量实在想要访问，可以设置 get 和 set。

### 获取对象信息

基本类型的数据可以用type()来判断。

对于class继承关系的来说，使用isinstance()来判定，某个实例是不是属于某个类。

要获得一个对象的所有属性和方法，可以使用一个dir()函数。
类似__xxx__的属性和方法在Python中都是有特殊用途的，比如__len__方法返回长度。在Python中，如果你调用len()函数试图获取一个对象的长度，实际上，在len()函数内部，它自动去调用该对象的__len__()方法。


### 面向对象高级编程

__slots__可以限制class的属性（由于动态语言的灵活性，我们可以随时给class或者实例绑定方法或者属性）达到限制的目的，Python允许在定义class的时候，定义一个特殊的__slots__变量，来限制该class能添加的属性。
	class Student(object):
		__slots__ = ('name','age')

但是对继承的子类是不起作用的。

### @property的使用

不太清楚这一块

### 多重继承

由于Python允许使用多重继承，因此，Mixin就是一种常见的设计。


### 使用元类

动态语言和静态语言最大的不同，就是函数和类的定义，不是编译时定义的，而是运行时动态创建的。




### 进程VS线程以及异步IO


scrapy基于twisted异步IO框架，downloader是多线程的。
但是，**由于python使用GIL（全局解释器锁，保证同时只有一个线程在使用解释器）**，这极大限制了并行性，在处理**运算密集型**程序的时候，Python的多线程效果很差，而如果开多个线程进行**耗时的IO操作**时，Python的多线程才能发挥出更大的作用。（因为Python在进行长时IO操作时会释放GIL）
所以简单的说，scrapy是多线程的，不许要再设置了，由于目前版本python的特性，多线程地不是很完全，但实际测试scrapy效率还可以。




### 分布式进程

可以分配到多个机器上。

Python的分布式进程接口简单，封装良好，适合需要把繁重任务分布到多台机器的环境下。

在Thread和Process中，应当优选Process，因为Process更稳定，而且，Process可以分布到多台机器上，而Thread最多只能分布到同一台机器的多个CPU上。

Python的**multiprocessing**模块不但支持多进程，其中managers子模块还支持把多进程分布到多台机器上。一个服务进程可以作为调度者，将任务分布到其他多个进程中，依靠网络通信。由于managers模块封装很好，不必了解网络通信的细节，就可以很容易地编写**分布式多进程程序**。

### HTMLParser

Python 提供来解析 HTMl 元素的，非常方便的解析 HTML 元素。它可以分析出 HTML 中的标签，数据。其采用一种事件驱动的模式，当其找到一个特定的标记时，会调用一个用户定义的函数，以此来通知程序处理。它主要的用户回调函数的命名都是以handler_开头的。包括：

* handle_startendtag  处理开始标签和结束标签
* handle_starttag     处理开始标签，比如<xx>
* handle_endtag       处理结束标签，比如</xx>
* handle_charref      处理特殊字符串，就是以&#开头的，一般是内码表示的字符
* handle_entityref    处理一些特殊字符，以&开头的，比如 &nbsp;
* handle_data         处理数据，就是<xx>data</xx>中间的那些数据
* handle_comment      处理注释
* handle_decl         处理<!开头的，比如<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"

* handle_pi           处理形如<?instruction>的东西

实例代码如下所示：


	from HTMLParser import HTMLParser
		from htmlentitydefs import name2codepoint
		class MyParser(HTMLParser):  
    		def __init__(self):  
        	HTMLParser.__init__(self)          
          
    	def handle_starttag(self, tag, attrs):  
        	if tag == 'a':  
           	 for name,value in attrs:  
                	if name == 'href':  
                    print value  

	if __name__ == '__main__':  
   		a = '<html><head><title>test</title><body><a href="http://www.163.com">链接到163</a></body></html>'  
    	my = MyParser()  
    	my.feed(a)  

实例二：

	from HTMLParser import HTMLParser
	from htmlentitydefs import name2codepoint

	class MyHTMLParser(HTMLParser):
    	def handle_starttag(self, tag, attrs):
       	   print('<%s>' % tag)
   	   	def handle_endtag(self, tag):
           print('</%s>' % tag)
    	def handle_startendtag(self, tag, attrs):
           print('<%s/>' % tag)
    	def handle_data(self, data):
           print('data')
    	def handle_comment(self, data):
           print('<!-- -->')
    	def handle_entityref(self, name):
           print('&%s;' % name)
    	def handle_charref(self, name):
           print('&#%s;' % name)

	parser = MyHTMLParser()
	parser.feed('<html><head></head><body><p>Some <a href=\"#\">html</a> tutorial...<br>END</p></body></html>')

综上其可作为轻量级程序处理，最好用的还是 BeautifulSoup。







