---
title: 建造者模式
date: 2016-05-13 15:19:35
tags: design-pattern
categories: About Java
---

设计模式之建造者模式

建造者模式可以将一个产品的内部表象与产品的生成过程分割开来，从而可以使一个建造过程生成具有不同内部表象的产品对象。

Builder 是一步一步创建一个**复杂的对象**，其允许用户只通过制定复杂对象的类型和内容就可以构建它们。用户不知道内部的具体构建细节。

引起的思考就是当设计的类有许多属性的时候，并且这些属性都处于一种可选的状态，用户可以自由定制，那么就可以使用建造者模式。

* Builder 建房子实例
* Builder 在 Android 中的应用即 AlertDialog,Retrofit
* Android 中的几大图片开源库都或多或少采用了 Builder pattern

<!-- more -->

[建造者模式](http://www.cnblogs.com/Coda/p/4283025.html)


### 一 . 定义

* 建造者(Builder)角色：给出一个抽象接口，以规范产品对象的各个组成成分的建造。
* 具体建造者(Concrete Builder)角色：实现 Builder 角色提供的接口，一步一步完成创建产品实例的过程。
* 指导者(Director)角色：导演者并没有具体产品类的知识，真正拥有产品类具体知识的是具体的建造者对象。
* 产品(Product)角色：产品便是建造中的复杂对象。

### 二 . 入门实例

``` java
// product
class Room{
     String window;
     String floor;
}
```

抽象建造者，定义一些方法

``` java
// builder interface
interface Builder{

    void makeWindow();
    void makeFloor();
    Room getRoom();
}
```

导演，即指挥者

``` java
// Dircetor
class Designer{
    // ask builder to work
    void order(Builder builder){
        builder.makeWindow();
        builder.makeFloor();
    }
}
```
实际工作的人

``` java
// real builder
class Worker implements Builder{

    private Room room = new Room();

    @Override
    public void makeWindow() {

        room.window = new String("window");
    }

    @Override
    public void makeFloor() {

        room.floor = new String("window");
    }

    @Override
    public Room getRoom() {
        return room;
    }
}
```
客户

``` java
// client
public class BuilderPattern {

    public static void main(String[] args) {

        Builder worker = new Worker();// get instance of worker
        Designer designer = new Designer();//get instance of designer
        designer.order(worker);
        worker.getRoom();// worker get the room and give to clinet
    }
}
```

### 三 . Builder 在 AlertDialog 中的应用

AlertDialog 属于无设计者(Director)

``` java

public class AlertDialog extends AppCompatDialog implements DialogInterface {

 	protected AlertDialog(Context context, int theme) {
        this(context, theme, true);
    }

	 @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mAlert.setTitle(title);
    }

 	public void setMessage(CharSequence message) {
        mAlert.setMessage(message);
    }

	public void setIcon(Drawable icon) {
        mAlert.setIcon(icon);
    }

 public static class Builder {
		// P is important
        private final AlertController.AlertParams P;

        private int mTheme;

        
        public Builder(Context context) {
            this(context, resolveDialogTheme(context, 0));
        }

        
        public Builder(Context context, int theme) {
            P = new AlertController.AlertParams(new ContextThemeWrapper(
                    context, resolveDialogTheme(context, theme)));
            mTheme = theme;
        }

        public Context getContext() {
            return P.mContext;
        }

        public Builder setTitle(int titleId) {
            P.mTitle = P.mContext.getText(titleId);
            return this;
        }
		
		public AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(P.mContext, mTheme, false);
            P.apply(dialog.mAlert);
            
            return dialog;
        }

        public AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
}
}
```

接着我们在代码中去构建 AlertDialog 实例，链式的调用，非常的优雅有木有：

``` java
// to build an instance of AlertDialog
new AlertDialog.Builder()
         .setTitle("title")
         .setIcon()
         .setNegativeButton()
		 .show();
```

### 四 . Builder 在 Retrofit 中的应用

Retrofit Builder 部分源码

``` java
  public static class Builder {
    private Endpoint endpoint;
    private OkHttpClient client;
    private Executor callbackExecutor;
    private RequestInterceptor requestInterceptor;
    private Converter converter;
    private ErrorHandler errorHandler;

    /** API endpoint URL. */
    public Builder setEndpoint(String url) {
       return setEndpoint(Endpoint.createFixed(url));
    }

    /** API endpoint. */
    public Builder setEndpoint(Endpoint endpoint) {
      if (endpoint == null) {
        throw new NullPointerException("Endpoint may not be null.");
      }
      this.endpoint = endpoint;
      return this;
    }

    /** The HTTP client used for requests. */
    public Builder setClient(OkHttpClient client) {
      if (client == null) {
        throw new NullPointerException("Client may not be null.");
      }
      this.client = client;
      return this;
    }

    public Builder setCallbackExecutor(Executor callbackExecutor) {
      if (callbackExecutor == null) {
        callbackExecutor = new Utils.SynchronousExecutor();
      }
      this.callbackExecutor = callbackExecutor;
      return this;
    }

    /** A request interceptor for adding data to every request. */
    public Builder setRequestInterceptor(RequestInterceptor requestInterceptor) {
      if (requestInterceptor == null) {
        throw new NullPointerException("Request interceptor may not be null.");
      }
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    /** The converter used for serialization and deserialization of objects. */
    public Builder setConverter(Converter converter) {
      if (converter == null) {
        throw new NullPointerException("Converter may not be null.");
      }
      this.converter = converter;
      return this;
    }

    public Builder setErrorHandler(ErrorHandler errorHandler) {
      if (errorHandler == null) {
        throw new NullPointerException("Error handler may not be null.");
      }
      this.errorHandler = errorHandler;
      return this;
    }
    /** Create the {@link RestAdapter} instances. */
    public RestAdapter build() {
      if (endpoint == null) {
        throw new IllegalArgumentException("Endpoint may not be null.");
      }
      ensureSaneDefaults();
      return new RestAdapter(endpoint, client, callbackExecutor, requestInterceptor, converter,
          errorHandler);
    }
  }
```

应用

``` java
RestAdapter adapter = new RestAdapter.Builder()
        .setEndpoint(HeadlineService.END_POINT)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setRequestInterceptor(new RequestInterceptor() {
          @Override public void intercept(RequestFacade request) {
            //TODO: intercept some request
          }
        }).setErrorHandler(new ErrorHandler() {
          @Override public Throwable handleError(RetrofitError cause) {

            //TODO: return some error 
            return null;
          }
        }).build();

        //TODO: use adaper
```

### 五 . 总结与参考

**为什么 Builder 要设计成静态的？**

使用static就如同你新建了一个JAVA文件，静态内部类不会保持对外部类的引用。如果你的builder需要外部类的引用，那它Build就没有意义了，所以必须加static

**Builder 的构造要求**
私有的构造方法;Builder需要静态内部class;创建对象前一定要保证参数健全

[参考](http://www.jianshu.com/p/e05bd950e513)

