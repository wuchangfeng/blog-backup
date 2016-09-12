---
title: Flask Web 开发数据库的设计
date: 2015-12-17 08:42:47
tags: flask
categories: About Python
---

Flask-Web开发笔记 五至七

<!-- more -->

####	第五章： 数据库 

* SQLAlchemy 是一个强大的非关系型 **数据库框架** ，支持多种**数据库后台**。SQLAlchemy 提供了高层 ORM,也提供了使用数据库原生SQL的底层功能。

* **定义模型**模型这个属于表示程序使用的持久化实体，在 ORM 中，模型一般是一个 Python 类，类中属性对应数据库表中的列。

* **关系** 关系型数据库使用关系把不同表中的行联系起来。比如说角色到用户的一对多关系，因为一个角色可以属于多个用户，而每个用户都只能有一个角色。

* 下面这段代码可以表示一对多关系在模型中的表示方法：


 
 * **使用 Flask-Migrate 实现数据库迁移**
 
   * 开发过程中，有时候需要修改数据库模型，修改之后需要更新数据库。而通过前面我们知道，数据库更新表的唯一方法就是删除旧表，在生成新表，这样不可避免会造成数据的丢失。更好的方法是使用数据库迁移框架。类似源码版本控制可以跟踪源码文件的变化，类似的数据迁移框架能跟踪数据库模式的变化，然后增量式的把变化应用到数据库中。
   
   * 创建迁移仓库
   
   * 创建迁移脚本
   
   * 更新数据库




####	第六章： 电子邮件

* 这一章使用 Gmail 会显得非常麻烦，自己改成了用163邮箱来发送信息,而对应的管理员邮箱为自己的 QQ 邮箱，这样别人一旦注册,评论,发表等就会邮件通知本人，配置在 Config 文件中，截图如下。

``` python 
class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'hard to guess string'
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True
    MAIL_SERVER = 'smtp.163.com'
    MAIL_PORT = 994
    MAIL_USE_TLS = False
    MAIL_USE_SSL = True
    MAIL_USERNAME = 'm13212109013'
    MAIL_PASSWORD = 'xxxxxxxxxxxx'
    FLASKY_MAIL_SUBJECT_PREFIX = '[Flasky]'
    FLASKY_MAIL_SENDER = 'm13212109013@163.com'
    FLASKY_ADMIN = 'wuchangfeng2015@gmail.com'
    FLASKY_POSTS_PER_PAGE = 10
    FLASKY_FOLLOWERS_PER_PAGE = 50
    FLASKY_COMMENTS_PER_PAGE = 30
```
####	第七章： 大型程序结构 
 
 * 使用程序工厂函数

在单个文件中开发程序很方便，但是由于程序在全局作用域中创建，所以无法动态修改配置。运行脚本时，程序实例都已经创建好了，在修改配置为时已晚。单元测试中，为了提供测试覆盖度，必须在不同的配置环境中去测试。

解决的办法就是**延迟程序实例的创建**，将创建过程显示的移到工厂函数中。这种方法不仅可以给脚本留出配置程序的时间，还可以创建多个程序实例。

程序包中的构造文件实例如下：

``` python
from flask import Flask
from flask.ext.bootstrap import Bootstrap
from flask.ext.mail import Mail
from flask.ext.moment import Moment
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.login import LoginManager
from flask.ext.pagedown import PageDown
from config import config

bootstrap = Bootstrap()
mail = Mail()
moment = Moment()
db = SQLAlchemy()
pagedown = PageDown()

login_manager = LoginManager()
login_manager.session_protection = 'strong'
login_manager.login_view = 'auth.login'
'''
程序包的构造文件
'''
'''
工厂函数，config_name即为程序的配置名称
'''
def create_app(config_name):
    app = Flask(__name__)
    app.config.from_object(config[config_name])
    config[config_name].init_app(app)

    # 初始化扩展
    bootstrap.init_app(app)
    mail.init_app(app)
    moment.init_app(app)
    db.init_app(app)
    login_manager.init_app(app)
    pagedown.init_app(app)

    # 注册蓝本
    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint, url_prefix='/auth')

    return app
```
 
 * 在**蓝本**中实现程序的功能


####   第九章： 用户角色

* 角色在数据库中的表示


* 赋予角色

一般人只要注册，就是默认角色。唯一有特殊的就是**管理员**，管理员在 FLASKY_ADMIN 中已经声明了，只要它的注册邮件出现在注册请求中，就会被赋予正确的角色。 

* 角色验证
