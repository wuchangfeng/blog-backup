---
title: Android-观察者模式应用
date: 2016-03-13 08:50:16
tags: android
categories: About Java
---



最近学习Head First 设计模式，学习到观察者模式，发现 Android 中的 OnClick() 事件采用的就是观察者模式，查了查书本，尝试记录整理一下。

<!-- more -->



![](http://7xrl8j.com1.z0.glb.clouddn.com/%E6%88%AA%E5%9B%BE%E4%B8%80.jpg)

如图所示，btn_shake 即 View 为被观察者，我们通过setOnclickListener() 方法来将 OnClickListener() 的一个对象即newOnclickListener(),注册到被观察者身上。


而当 btn_shake 即 View，这个被观察者，被点击时，就会回调已经注册成为观察者的onCLickListener() 对象中的 Onclick() 方法来通知观察者。

![](http://7xrl8j.com1.z0.glb.clouddn.com/%E6%88%AA%E5%9B%BEer.jpg)

上图即 View 中定义的 OnClickListener() 这个观察者接口，以及里面需要实现的方法。


**注意两点**

* OnClickListener() 按理来讲不是 View 中定义的接口吗？为什么我们可以 new 出它的对象呢？没错，接口本身是无法实例化的，new 的形式实际上相当于创建了一个这个这个接口的类对象，必须实现接口中的方法，并且 new 出来的这个对象是匿名的。


* 为什么调用 OnClick() 这个方法称为回调来通知观察者呢？主要是虽然 Onclick() 实现在 MainActicity 中，但实际上还是在 View 中来调用的，这一点可以看源码。


以上即为观察者模式在 Android 中 OnClick() 事件的小应用。
具体的，稍微复杂点的实例我们也可以参考 Android群英传的第47页。

