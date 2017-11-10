---
title: 设计模式之观察者模式
date: 2015-11-23 15:30:48
tags: design-pattern
categories: About Java
---

观察者模式就像一种订阅的机制，客户对什么信息内容感兴趣，并且不想错过这些内容，则可以订阅这些消息，一旦有新的消息发布，客户就会接收到最新的消息，当然当你不厌其烦的时候，可以选择取消订阅。观察者模式是JDK源码中应用的比较多的。这一章还需要深入学习的就是松耦合设计，高效的松耦合可以设计出非常干净的面向对象程序。

### 角色定义

* 抽象被观察者：抽象被观察者类提供一系列接口，可以增加、删除、通知观察者对象。
* 抽象观察者：观察者角色一般是一个接口，它只有一个update方法，在被观察者状态发生变化时，这个方法就会被触发调用。
* 具体的被观察者：使用这个角色是为了便于扩展，可以在此角色中定义具体的业务逻辑
* 具体的观察者：察者接口的具体实现，在这个角色中，将定义被观察者对象状态发生变化时所要处理的逻辑


### 实例引入 

具体场景就是气象站要向订阅了天气信息的观察者发送信息。下面通过代码来讲解整个实例过程：

下面分别是主题接口、观察者接口、显示事件接口：
``` java
interface Subject{
    // 注册成为观察者
	public void registerObserver(Observer o);
    // 移除观察者
	public void removeObserver(Observer o);
    // 通知观察者
	public void notifyObserver();
}

// 观察者
interface Observer{
	public void update(float temp,float humidity,float pressure);
}

// 显示事件
interface DisplayElement{
	 public void display();
}	
````

天气数据实现主题Subject接口：
``` java
class WeatherData implements Subject{
	    private ArrayList observers;
	    private float temperature;
	    private float humidity;
	    private float pressure;

	    public WeatherData() {
			// 获得订阅者列表
	        observers = new ArrayList();
	    }

	    @Override
	    public void registerObserver(Observer o) {
	        observers.add(o);
	    }

	    @Override
	    public void removeObserver(Observer o) {
	        int i = observers.indexOf(o);
	        if (i >= 0)
	            observers.remove(i);            
	    }

		// 通知订阅者
	    @Override
	    public void notifyObserver() {
	        for (int i = 0; i < observers.size(); i++) {
	            Observer observer = (Observer) observers.get(i);
	            observer.update(temperature,humidity,pressure);
	        }
	    }

		// 通知订阅者
	    public void measurementsChanged(){
	        notifyObserver();
	    }

		// 温度变化
	    public void setMeasurements(float temperature,float humidity,float 	pressure){
	        this.temperature = temperature;
	        this.humidity = humidity;
	        this.pressure = pressure;
	         measurementsChanged();
	    }
}	
```

当前天气显示布告板，也就是观察者层面：
``` java
class CurrentConditionsDisplay implements Observer,DisplayElement{
	    private float temperature;
	    private float humidity;
	    private Subject weatherData;

	    public CurrentConditionsDisplay(Subject weatherData) {
	        this.weatherData = weatherData;
	        //weatherData.notifyObserver();
	        weatherData.registerObserver(this);
	    }

	    @Override
	    public void display() {
	        System.out.println("当前的温度情况：" + temperature + "湿度情况:" + humidity);
	    }

	    @Override
	    public void update(float temp, float humidity, float pressure) {
	        this.temperature = temp;
	        this.humidity = humidity;
	        display();
    	}
}	
```

天气播报中心：
``` java
public class WeatherStation {
    	public static void main(String[] args) {

        WeatherData weatherData = new WeatherData();
        CurrentConditionsDisplay currentConditionsDisplay = new CurrentConditionsDisplay(weatherData);

        weatherData.setMeasurements(80,65,30.4f);
        weatherData.setMeasurements(81,65,30.4f);
        weatherData.setMeasurements(82,65,30.4f);
    	}
}	
```

具体涉及场景以及实现思路可以见书上，这里的重点就是：主题(被观察者)主动推送更新的数据给观察者。

### 升级代码

利用java内置的观察者接口可以写成如下形式：
     
接口声明：	 
``` java
import java.util.Observable;
import java.util.Observer;
//接口，任何一种布告板需要若实现该接口，都需要实现其中的方法
interface DisplayElements{
    public void display();
}
```

数据源实现被观察者接口：
``` java
class WeatherDatas extends Observable{
    private float temp;
    private float hum;
    private float pre;

    public WeatherDatas() {
    }

    public void measurementsChanged(){
        setChanged();
        notifyObservers();//我们可以点进去查看源码,发现如果有改变，会一个一个通知观察者。
    }

    public void setMeasurements(float temp,float hum,float pre){
        this.temp = temp;
        this.hum = hum;
        this.pre = pre;
        measurementsChanged();
    }

    public float getTemp() {
        return temp;
    }

    public float getHum() {
        return hum;
    }

    public float getPre() {
        return pre;
    	}
	}
```

布告板实现具体接口：
```java
class CurrentConditionsDisplays implements Observer,DisplayElements{
     Observable observable;
     private float temp;
     private float hum;

     public CurrentConditionsDisplays(Observable observable) {
         this.observable = observable;
         observable.addObserver(this);
     }

     @Override
     public void display() {
         System.out.println("当前温度：" + temp);
     }
     //@Override
     public void update(Observable obs,Object obj) {

         if (obs instanceof WeatherDatas){
             WeatherDatas weatherDatas = (WeatherDatas) obs;
             this.temp = weatherDatas.getTemp();
             this.hum = weatherDatas.getHum();
             display();
         }
	     }
	 }
```

气象预报站：
``` java
public class WeatherObservableStation {

	public static void main(String[] args) {
        WeatherDatas weatherDatas = new WeatherDatas();
        // 当前温度布告板，还可以有预测布告板，等等
        CurrentConditionsDisplays currentConditionsDisplays =
                new CurrentConditionsDisplays(weatherDatas);
        weatherDatas.setMeasurements(80,56,40.3f);
        weatherDatas.setMeasurements(10,56,40.3f);
        weatherDatas.setMeasurements(20,56,40.3f);
	    }
}	
```

具体实现类图，可以参考 Head-first-设计模式第二章。

### 分析思考

实现的过程，会有点疑惑例如利用Java内置的接口来实现观察者模式，其中最重要的思想是由观察者自己来"拉取数据"，而不是由被观察者主动推送。被观察者只需要通知观察者“我的数据准备好了，你们来取吧。”这些可以理解，我们用get方法去取，但是不得不问一下，被观察者怎么去通知观察者？

	setChanged();
    notifyObservers()

因为这两个方法是自动生成的，我们自然由其入手，双击shift进入notifyObservers()源码中,发现：

``` java
public void notifyObservers(Object arg) {
    Object[] arrLocal;
    synchronized (this) {
        if (!changed)
             return;
        arrLocal = obs.toArray();
        clearChanged();
    }
    for (int i = arrLocal.length-1; i>=0; i--)
     ((Observer)arrLocal[i]).update(this, arg);
}		
```

如下所示一部分源码，自然可以清楚怎么去通知的了，原来**被观察者**调用了所有**观察者**的update()。另外一个重点就是,setChanged() 方法是被保护起来的。以及 Observable 是一个类，而不是一个接口。引起了一些问题。见书上 P71。
    
``` java
protected synchronized void setChanged() {
    changed = true;
}	
```

另外，该模式在jdk中应用于Swing中较多。

### 观察者模式优缺点

两个对象之间松耦合，他们之间依然可以交互，只是不太清楚彼此的细节，观察者提供了一种对象设计，让主题和观察者之间松耦合。关于观察者的一切，主题只知道观察者实现了某个接口，主题不需要观察者的具体类是谁，做了什么或者其他细节。任何时候,我们可以随意增加观察者，主题唯一依赖的就是一个观察者列表。
总之改变主题或者观察者其中的一方，并不会影响另一方。因为两者是松耦合的，所以只要他们之间的接口仍然被遵循，我们可以自由的改变他们。所以**松耦合**也是我们追求的目标。


	

