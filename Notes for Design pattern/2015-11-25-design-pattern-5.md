---
title: 设计模式之责任链模式
date: 2015-11-25 15:33:38
tags: design-pattern
categories: About Java
---

使多个对象都有机会处理请求，从而避免了请求的发送者和接收者之间的耦合关系。将这些对象连成一条链，并沿着这条链传递该请求，直到有对象处理它为止。

### 角色定义

* 抽象处理类：抽象处理类中包含指向下一处理类的成员变量nextHandler和一个处理请求的方法handRequest,handRequest能处理的话自己处理，不能处理的话交给nextHandler来处理

* 具体处理类：具体处理类主要是对具体的处理逻辑和处理的适用条件进行实现。

### 代码讲解

定义事件等级：
``` java
class Level {
    private int level = 0;
	// 赋予当前等级
    public Level(int level){
        this.level = level;
    };
    // 判断请求等级与当前处理器等级大小
    public boolean above(Level level){
        if(this.level >= level.level){
            return true;
        }
        return false;
    }
}
```

定义Request请求：
``` java
class Request {
    Level level;
	// 获取请求等级
    public Request(Level level){
        this.level = level;
    }
    
    public Level getLevel(){
        return level;
    }
}
```

定义Response响应：
``` java
class Response {
  
}
```

定义抽象处理器：
``` java
abstract class Handler {
    private Handler nextHandler;

	// 事件处理器
    public final Response handleRequest(Request request){
        Response response = null;
        // 如果当前处理类的等级高于Request的等级才能处理
        if(this.getHandlerLevel().above(request.getLevel())){
            response = this.response(request);
        }else{ // 交给下一个处理者
                if(this.nextHandler != null){
                    this.nextHandler.handleRequest(request);
                }else{
                    System.out.println("-----没有合适的处理器-----");
                }
            }
            return response;
        }
    public void setNextHandler(Handler handler){
        this.nextHandler = handler;
    }
    protected abstract Level getHandlerLevel();
    public abstract Response response(Request request);
}
```

三个事件处理者：
``` java
// 事件处理者1
class ConcreteHandler1 extends Handler {
        protected Level getHandlerLevel() {
            return new Level(1);
        }
        public Response response(Request request) {
            System.out.println("-----请求由处理器1进行处理-----");
            return null;
        }
}

// 事件处理者2
class ConcreteHandler2 extends Handler {
        protected Level getHandlerLevel() {
            return new Level(3);
        }
        public Response response(Request request) {
            System.out.println("-----请求由处理器2进行处理-----");
            return null;
        }
}

// 事件处理者3
class ConcreteHandler3 extends Handler {
        protected Level getHandlerLevel() {
            return new Level(5);
        }
        public Response response(Request request) {
            System.out.println("-----请求由处理器3进行处理-----");
            return null;
        }
}
```

客户端测试类：
``` java
public class Client {
    public static void main(String[] args){
        Handler handler1 = new ConcreteHandler1();
        Handler handler2 = new ConcreteHandler2();
        Handler handler3 = new ConcreteHandler3();

        handler1.setNextHandler(handler2);
        handler2.setNextHandler(handler3);

        Response response = handler1.handleRequest(new Request(new Level(4)));
        }
    }
```

### 责任链模式优缺点

责任链模式与if…else…相比，他的耦合性要低一些，因为它把条件判定都分散到了各个处理类中，并且这些处理类的优先处理顺序可以随意设定。责任链模式也有缺点，这与if…else…语句的缺点是一样的，那就是在找到正确的处理类之前，所有的判定条件都要被执行一遍，当责任链比较长时，性能问题比较严重。