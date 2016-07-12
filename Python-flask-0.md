---
title: Flask Web 开发入门
date: 2015-12-16 08:42:47
tags: flask
categories: About Python
---

Flask-Web开发笔记 一至四

<!-- more -->

#	前言

学习两个星期的 Flask 开发之后，顺利按照课本上的步骤做出一个社交博客。现在将开发过程遇到一些问题的记录。方便自己以后查阅以及帮助后学者跳过不必要的坑。


##	资料推荐

* 廖雪峰官网   Web 开发

* Flask Web 开发 基于 Python 的 Web 应用开发实战

* Flask 中文文档

##	开发环境

* Python 2.7

* Sublime Text3

* Git Bash

##	基础部分

<!--more-->

####	第一章： 安装 

* 安装虚拟环境 Virtualenv,主要为了方便的安装 Flask 模块。虚拟环境是 Python 解释器的一个**副本**，在这个环境中，可以安装私有包。具体安装过程可以 Google。

* 执行<pre><code>（venv）$ pip install flask</code></pre> 可以在 Python shell 中利用**import flask**检验是否安装成功。

* **pip**非常有用，可以非常便捷的安装第三方扩展模块。先安装**easy_install**接着安装 pip。

* 如果在 Git bash 中进入不了虚拟环境，可以利用**cmd**命令行来进入含有activate目录的文件夹，接着启动虚拟环境，进入虚拟环境后，可以 <pre><code>python manage.py runserver</code></pre>启动服务。

####	第二章： 程序的基本结构 

* 路由和视图函数

客户端(Web 浏览器)把请求发送给 web 服务器，Web 服务器把请求发送给 flask 实例。程序实例需要知道每个请求 url 对应哪一段代码，所以**保存了一段 url 到 Python程序的映射关系**，处理 url 和函数之间的关系的程序称为**路由**

* 一个完整的程序结构

* 请求-响应循环

 * current_app:程序上下文。当前激活程序的程序实例。
 * g:程序上下文。处理请求时用作临时存储的对象。每次请求都会重设这个变量。
 * request:请求上下文。请求对象，封装了客户端发出的 HTTP 请求中的内容。
 * session：请求上下文。用于存储请求之间需要"记住"的值的字典。

* Flask 扩展

####	第三章：  模板 

* 主要作用可以简单理解为 **把业务逻辑（Python）和表现逻辑（HTML）分开**

* **渲染模板** 即将具体数值传入到含有 **占位符** 的模板中，其返回最终得到的响应字符串。为了渲染模板，Flask 使用了一个名为 Jinja2 的强大模板引擎。其提供的 **render_template** 函数将Jinja2模板引擎集成到了程序中。通常的程序为(即渲染了 index.html 这个模板)：

``` python
	return render_template('index.html')
```
	

* 模板放置于 Flask 顶级文件夹中的 templates 文件夹中，Css，Js放置于 static 文件夹中。

* 模板中的{{name}}表示一个变量，是一种特殊的占位符，告诉模板引擎该值从渲染模板时使用的数据中获取。另外 jinja2 还提供了 **变量过滤器**。

* 代码复用

  * 需要在多处重读使用的模板代码片段可以写入单独的文件,再包含在所有的文档中，如<pre><code>{% include 'common.html' %}</code></pre>
  
  * 另一种方式就是模板继承，需要在子类中用到的公用代码就写进 base.html 中,需要用到这些代码的，可以执行 <pre><code>{% extends "base.html" %}</code></pre>,继承 base.html 的衍生模板，可以修改覆盖所继承的模板中 block 标签定义的元素。当然如果不想覆盖，可以直接 super()获 取原来的内容。
  
  * 实例如下
  
  * ![](http://7xrl8j.com1.z0.glb.clouddn.com/block.jpg)
  
* Bootstrap 框架,Flask 中集成为 Flask-Bootstrap。**Bootstrap是客户端框架**，因此不会涉及服务器。服务器要做的只是提供引用了 Bootstrap 层叠样式表和 JavaScript 文件的HTML 响应，并在 HTML,CSS 和 JavaScript 代码中实例化所需要的组件。这些操作最理想的执行场所就是模板。 

* 类似于代码复用，我们采用如下格式复用 Bootstrap 提供的模板<pre><code>{% extends "bootstrap/base.html" %}</code></pre>

* 另外如果想要在衍生模板中添加新的 JavaScript 文件，那么需要这样定义 scripts 块。

  * ![](http://7xrl8j.com1.z0.glb.clouddn.com/bootscript.jpg)

* 链接

程序包含大量路由链接，需要来回跳转，直接编写 url 当然可以，但是这样动态性不强，而且如果根链接改动了，到处引用的都需要去改动，很是麻烦，所以有了 url_for() 辅助函数，如 url_for('index') 得到的是 www.example.com/
 
####	第四章： Web表单 

 * **跨站请求伪造保护** 默认情况下 Flask-WTF 能保护所有表单免收 CSRF 攻击。恶意网站把请求发送到被攻击者已登录的其他网站就会引发CSRF攻击。
 
 * 为了能轻松的渲染表单 Flask——Bootstrap 提供了一个非常高端的函数，可以使用Bootstrap 中预先定义好的表单样式渲染整个 Flask-WTF，这些操作只需要一次调用即可完成。

  * ![](http://7xrl8j.com1.z0.glb.clouddn.com/wtf.jpg)
  
 * 重定向 redirect(),用户会话 session(),其中用户会话是一种私有操作，用户在请求之间 **记住数据**。

 * 在视图函数中处理表单

``` python
	@app.route('/', methods=['GET', 'POST'])
	def index():
   	 	form = NameForm()
    	if form.validate_on_submit():
        	old_name = session.get('name')
        	if old_name is not None and old_name != form.name.data:
            	flash('Looks like you have changed your name!')
        	session['name'] = form.name.data
        	return redirect(url_for('index'))
   	 return render_template('index.html', form=form, name=session.get('name'))
```
 
说一下它的处理流程：

web 浏览器 -url-> web 服务器 -url-> flask程序实例-->找到 index 函数--> 获取到表单函数--> 渲染出 HTML 界面展示在 web浏览器

另外在这里说一下：仅仅调用 flash 函数并不能显示出消息，程序使用的模板要渲染这些消息，最好在基模板中渲染，这样所有程序都能看到这些消息。



 * Flash消息，就像google chorme中经常提示的那个东西,一种友好的提示。**注意** 仅在程序中调用 flash() 函数并不能显示该消息，程序中使用的模板要 **渲染** 这些消息，并且要在基模板中渲染。
 





