---
title: 观察者模式
date: 2016-04-01 15:30:48
tags: design-pattern
categories: About Java
---

Head first 设计模式-观察者模式

观察者模式就像一种订阅的机制，你对什么信息内容感兴趣，而不像错过这些，则可以订阅这些消息，一旦有新的消息发布，你就会接收到最新的消息，当然当你不厌其烦的时候，可以选择取消订阅。

这个模式是 JDK 源码中应用的比较多的。

这一章还需要深入学习的就是松耦合设计，高效的松耦合可以设计出非常干净的 OO 程序。

<!-- more -->

### 一 . 实例引入 

具体场景就是气象站要向订阅了天气信息的观察者发送信息。

先上代码：

``` java
interface Subject{
	    public void registerObserver(Observer o);
	    public void removeObserver(Observer o);
	    public void notifyObserver();
}

interface Observer{
	    public void update(float temp,float humidity,float pressure);
}

interface DisplayElement{
	    public void display();
}	
````

``` java
class WeatherData implements Subject{
   
	    private ArrayList observers;
	    private float temperature;
	    private float humidity;
	    private float pressure;

	    public WeatherData() {
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

	    @Override
	    public void notifyObserver() {
	        for (int i = 0; i < observers.size(); i++) {
	            Observer observer = (Observer) observers.get(i);
	            observer.update(temperature,humidity,pressure);
	        }
	    }

	    public void measurementsChanged(){
	        notifyObserver();
	    }

	    public void setMeasurements(float temperature,float humidity,float 	pressure){
	        this.temperature = temperature;
	        this.humidity = humidity;
	        this.pressure = pressure;
	         measurementsChanged();
	    }
}	
```

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

### 二 . 升级代码

利用java内置的观察者接口可以写成如下形式：
     
``` java
import java.util.Observable;
	import java.util.Observer;

	//接口，任何一种布告板需要若实现该接口，都需要实现其中的方法

	interface DisplayElements{
    	public void display();
	}

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

### 三 . 分析思考

实现的过程，会有点疑惑，比如一点，这种利用 java 内置的接口来实现观察者模式，其中最重要的思想是由观察者自己来"拉取数据"，而不是由被观察者主动推送。被观察者只需要通知观察者，我的数据准备好了，你们来取吧。这些我们可以理解，我们用 get 方法去取，但是我们不得不疑问一下，被观察者怎么去通知观察者？

	setChanged();
    notifyObservers()

因为这两个方法是自动生成的，我们自然由其入手，双击 shift 进入 notifyObservers()源码中,发现：


``` java
public void notifyObservers(Object arg) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

        synchronized (this) {
            /* We don't want the Observer doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each Observable from
             * the Vector and store the state of the Observer
             * needs synchronization, but notifying observers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added Observer will miss a
             *   notification in progress
             * 2) a recently unregistered Observer will be
             *   wrongly notified when it doesn't care
             */
            if (!changed)
                return;
            arrLocal = obs.toArray();
            clearChanged();
        }

        for (int i = arrLocal.length-1; i>=0; i--)
            ((Observer)arrLocal[i]).update(this, arg);
}		
```

如下所示一部分源码，自然可以清楚怎么去通知的了，原来**被观察者**调用了所有**观察者**的 update()。


另外一个重点就是,setChanged() 方法是被保护起来的。以及 Observable 是一个类，而不是一个接口。引起了一些问题。见书上 P71。
    
``` java
protected synchronized void setChanged() {
        changed = true;
}	
```

另外，该模式在 jdk 中 应用于 swing 中较多。

### 四 . 总结

两个对象之间松耦合，他们之间依然可以交互，只是不太清楚彼此的细节，观察者提供了一种对象设计，让主题和观察者之间松耦合。

关于观察者的一切，主题只知道观察者实现了某个接口，主题不需要观察者的具体类是谁，做了什么或者其他细节。任何时候,我们可以随意增加观察者，主题唯一依赖的就是一个观察者列表。

总之改变主题或者观察者其中的一方，并不会影响另一方。因为两者是松耦合的，所以只要他们之间的接口仍然被遵循，我们可以自由的改变他们。

所以**松耦合**也是我们追求的目标。


	

