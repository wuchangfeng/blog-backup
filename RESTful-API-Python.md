---
title: 给 Flasky 设计 RESTful API 
date: 2016-5-13 14:37:20
tags: flask
categories: About Python
---

在 Web 中资源就是一切，一个网站没有 API 接口，没得玩了吧？资源是 REST 架构中核心的概念，因此 Flasky 也要进化了，这一章介绍 REST API 是概念以及其在 Flask 中的应用。


下面是阮一峰以及 Python 官方关于 RESTful API 的一些介绍：

[RESTful API 的架构理解](http://www.ruanyifeng.com/blog/2011/09/restful.html)

[RESTful API 设计指南](http://www.ruanyifeng.com/blog/2014/05/restful_api.html) 

[Python RESTful 中文扩展文档](https://wizardforcel.gitbooks.io/flask-extension-docs/content/flask-restful.html)


<!-- more -->



### 一. RESTFUL API 定义

Web 服务的 REST 架构方式，以下为六个符合的特征：

* 客户端-服务器：客户端和服务器之间必须有明确的界限
* 无状态：客户端发出的请求中必须包含所有必要的信息，服务器不能在两次请求之间保存客户端的状态。
* 缓存：服务器发出的响应，可以标记为可缓存或者不可缓存，这样出于优化目的，客户端可以使用缓存。
* 接口统一：客户端访问服务器资源时使用的协议必须一致，定义良好，且已经标准化。REST Web 服务是最长使用的统一接口是HTTP协议。
* 系统分层：在客户端和服务端之间可以按需插入代理服务器，缓存或者网关，提供性能。
* 按需代码：客户端可以从服务器中下载代码，在客户端中运行。 


请求方法： post get put delete 这四种方法，具体的可以参见上面博客链接。 如下图：

![](http://7xrl8j.com1.z0.glb.clouddn.com/http.jpg)


### 二. 使用 Flask 提供的 REST Web 服务

核心就是使用 route() 修饰器以及其 methods 可选参数可以声明服务所提供资源的 URL 路由，处理 Json 数据同样很简单。

#### 2.1 创建 API 蓝本

API 的蓝本

``` python
from flask import Blueprint

api = Blueprint('api', __name__)

from . import authentication, posts, users, comments, errors
```

API 蓝本的注册

``` python
from .api_1_0 import api as api_1_0_blueprint

app.register_blueprint(api_1_0_blueprint, url_prefix='/api/v1.0')
```

注册蓝本统一在工厂函数中注册，不要忘了。

#### 2.2 错误处理

#### 2.3 使用 Flask-HTTPAuth 认证用户

REST Web 服务的特征之一就是无状态，即**服务器在两次请求之间不能记住客户端的任何信息**。客户端必须在发出请求中包含所有必要的信息，因此所有请求都必须包含用户密令。

默认情况下 Flask 把会话保存在客户端 cookie 中，因此服务器没有保存任何用户相关信息，都转给客户端来保存。这种保存方式看起来遵守了 REST 架构无状态要求，但是在 REST Web 服务中使用 cookie 有点不现实，因为我们除了要支持浏览器外，还要支持别的客户端。

结合上述原因以及开发平台，选择了 flask-httpauth 这和扩展库来进行用户的验证。

#### 2.4 基于令牌的认证

认证令牌即 token 应该很熟悉吧，即客户端每一次请求时，都要发送认证密令，为了避免每次都发送敏感信息，可以提供一种基于令牌的认证方案。下面程序可以详细解释如何操作。

``` python
def generate_auth_token(self, expiration):
        s = Serializer(current_app.config['SECRET_KEY'],
                       expires_in=expiration)
        return s.dumps({'id': self.id}).decode('ascii')

@staticmethod
def verify_auth_token(token):
        s = Serializer(current_app.config['SECRET_KEY'])
        try:
            data = s.loads(token)
        except:
            return None
        return User.query.get(data['id'])

def __repr__(self):
        return '<User %r>' % self.username
```

#### 2.5 资源和 JSON 的序列化转换

``` python
def to_json(self):
    json_post = {
            'url': url_for('api.get_post', id=self.id, _external=True),
            'body': self.body,
            'body_html': self.body_html,
            'timestamp': self.timestamp,
            'author': url_for('api.get_user', id=self.author_id,
                              _external=True),
            'comments': url_for('api.get_post_comments', id=self.id,
                                _external=True),
            'comment_count': self.comments.count()
        }
     	return json_post
```


这里有一点要注意，构造上面这些字段时，并不需要完全跟 post 模型或者其对应的数据库字段一致，只需要提供我们想要提供的，甚至可以虚造字段比如 comment_count 就是评论的数量。

#### 2.6 实现资源端点

这里就是实际的 url 请求的地方，由此可见利用 flask 的 route 路由来设计 api 是非常方便的。

下为文章资源 GET 请求的处理程序

``` python 
'''
 返回所有文章集合 json 数据
'''
@api.route('/posts/')
def get_posts():
    page = request.args.get('page', 1, type=int)
    pagination = Post.query.paginate(
        page, per_page=current_app.config['FLASKY_POSTS_PER_PAGE'],
        error_out=False)
    posts = pagination.items
    prev = None
    if pagination.has_prev:
        prev = url_for('api.get_posts', page=page-1, _external=True)
    next = None
    if pagination.has_next:
        next = url_for('api.get_posts', page=page+1, _external=True)
    return jsonify({
        'posts': [post.to_json() for post in posts],
        'prev': prev,
        'next': next,
        'count': pagination.total
    })
'''
 返回指定文章
'''
@api.route('/posts/<int:id>')
def get_post(id):
    post = Post.query.get_or_404(id)
    return jsonify(post.to_json())
```


### 三. 使用 HTTPie 测试 Web 服务

测试 Web 服务肯定要 HTTP 客户端来。选择 HTTPie 来进行测试。

	pip install httpie

