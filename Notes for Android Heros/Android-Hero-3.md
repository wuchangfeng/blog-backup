---
title: Android-群英传读书笔记（3）
date: 2016-03-21 11:12:29
tags: android
categories: About Java
---

Android 控件架构与自定义控件详解

<!-- more -->

### Android 控件架构

通常在 Activty 中使用的 FindViewById() 的方法，就是在控件树中以树的深度优先遍历来查找对应元素。在每颗控件树顶部，都有一个 ViewParent 对象，这就是整个树核心。



每个 Activity 都包含一个 Window 对象，在 Android 中 Window 对象通常由 PhoneWindow 对象来实现。 PhoneWindow 将一个 DecorView 设置为整个应用窗口的根 View。其作为窗口界面的顶层视图,封装了一些窗口通用的操作方法。DecorView 将要显示的内容具体显示在了 PhoneWindow 上，这里的所有 View 的监听事件，都通过 WindowManagerService 来接受，并且通过 Activity 对象来回调相应的 onClickListener。在显示上，它将屏幕分成两部分，一个是 TitleView，另一个是 ContentView。 这个都熟悉，它是一个 ID 为 content 的 FrameLayout，activity_main.xml 就是设置在这样一个 FrameLayout 中。

另外在代码中，当程序在 onCreate() 方法中调用 setContentView() 后，ActivityManagerService 会回调 onResume() 方法，此时系统会把整个 DecorView 添加到 PhoneWindow 中，并让它显示出来，从而完成界面的绘制。

### View 的测量

测量 View 在 onMeasure() 中进行。其中 MeasureSpec 类起了很大作用。它是一个 32 位的int值，高2位为测量模式，低30位为测量的大小。
分为以下三种：

* EXACTLY

当我们把控件值设为具体值时候: android:layout_width="100dp",或者指定为match_parent属性(占据父View的大小)，系统使用的是改模式。

* AT_MOST

即最大值模式，当控件的layout_width属性或者layout_height属性指定为wrap_content时，空间大小一般随着控件的子控件或者内容的变化而变化，此时控件的尺寸只要不超过父控件允许的最大尺寸即可。

* UNSPECIFIED

不指定，想多大就多大。


View 类默认的 onMeasure() 方法只支持 EXACTLY 模式，所以自定义控件时，为了能让你的View 支持 wrap_content 属性，那么最好是重写 onMeasure() 来指定 wrap_contents 的大小。

简单实例，演示如何进行 View 的测量：

``` java
@Override
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)；
}
```
查看 super.onMeasure()方法，发现最终调用 setMeasuredDimension(int measuredWidth,int measuredHeight)方法将测量后的宽高设置进去，所以重写之后最重要的工作就是把参数传入 setMeasuredDimension() 中,通过以上分析，重写代码如下：

``` java
@Override
protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		setMeasuredDimension(measureWidth(measuredWidth),measureHeight(measuredHeight));
}
```

下面以measureWidth()为例：
第一步，从measureSpec对象中获取具体的测量模式和大小：

``` java
int specMode = MeasureSpec.getMode(measureSpec);
int specSize = MeasureSpec.getSize(measureSpec);
```

第二步，通过判断测量模式，给出不同的测量值,如下可作为代码模板：

``` java
private int measureWidth(int measureSpec){
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if(sizeMode == MeasureSpec.EXACTLY){
			result = specSize;
		}
		else{
			result = 200;
			if(specMode == MeasureSpec.AT_MOST){
				result = Matn.min(result,specSize);
			}
		}
	return result;
}
```


说明：当specMode为其他两种模式时候，需要给它一个默认大小。特别的，如果指定了wrap_content属性，即AT_MOST模式，则需要取出我们指定的大小与specSize之间最小的一个来指定测量值。

### View 的绘制

重写View中的onDraw()方法。

### ViewGroup 的测量

### ViewGroup 的绘制

### 自定义 View


* 创建复合控件

这种方式通常继承一个合适的 ViewGroup ，再给它指定功能的控件。一般都会给其指定一些可配置的属性，让其具有更灵活的扩展性。

实例为TopBar：

创建一个灵活的UI模板，其应该具有通用性和可扩展性，即给调用者以丰富的接口，让他们可以更改模板中的文字，颜色，行为等信息。

  * 定义属性
  为View提供一些可自定义的属性，在res的资源文件values目录下指定即可。一个attr.xml的属性定义文件。
  
  * 组合控件
  确定好属性之后，就可以创建一个自定义控件-TopBar,其继承自ViewGroup，从而组合一些需要的控件。并且在构造方法中，通过如下代码获取在XML文件中自定义的属性。
	
``` java
TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TopBar);
```
  
然后我们就可以利用ta对象来获取需要的属性了

``` java	
mLeftTextColor = ta.getColor(R.styleable.TopBar_LeftTextColor,0); 
//在获取属性之后一般要调用recycle()方法来避免重新创建的时候错误
ta.recycle();
```

组合控件，实际上UI模板又三个部分组成，左右点击按钮以及中间的title。我们使用动态添加的方式将这三个view添加进去。即AddView()方法。并且给他们设置一些我们之前获取到的属性。
  
既然是模板，所以不能再UI模板中添加直接的实现逻辑，只能通过接口回调的思想，将具体的实现交给调用者。实现过程如下所示：

* 定义接口：

在UI模板中定义一个左右按钮点击的接口，并且创建两个方法。分别用于左右按钮的点击。

``` java
// 接口对象，实现回调机制，在回调的方法中，通过映射的接口对象调用接口中的方法
	public interface topbarClickListener{
			void leftClick();
			void rightClick();
}
```

* 暴露接口给调用者：

在模板方法中，为左右按钮增加点击事件，但是不去实现具体的逻辑，而是调用接口中相应的点击方法：

``` java
// 按钮的点击事件，不需要具体的实现，只需调用接口中的方法，回调的时候会有具体的实现
mRightButton.setOnClickListener(new OnClickListener(){
		@override
		public void onClick(View v){
			mListener.rightClick();
	}

	});

mLeftButton.setOnClickListener(new OnClickListener(){
		@override
		public void onClick(View v){
			mListener.leftClick();
	}

	});

// 暴露一个方法给调用者来注册接口回调，通过接口来获得回调者对接口方法的实现
public void setOnTopbarClickListener(topbarClickListener mListener){
		this.mListener = mListener;
	}
```

* 实现接口回调：

在调用者的代码中，调用者需要实现这样一个接口，并且完成接口中的方法，确定具体的接口实现逻辑，并且使用上述步骤中暴露的方法，**将接口对象**传递进去，从而完成回调。通常情况下可以使用**匿名内部类**的形式来实现接口中的方法：

``` java
mTopbar.setOnTopbarClickListener(
		new TopBar.topbarClickListener(){
		
		@override
		public void rightClick(){
			//todo
		}
      
        @override
		public void leftClick(){
			//todo
		}
});
```


* 引用UI模板：

最后一步就是引用第三方控件，需要指定名字，在 Android 系统中，我们经常可以看见：

``` xml
xmlns: android="http://schemas.android.com/apk/res/android"
```
这里指定了名字控件，因此接下来才可以使用系统的属性，如 android:。因此如果要使用自定义的属性，就需要自己创建名字控件，如下：

``` xml
xmlns: custom="http://schemas.android.com/apk/res-auto"
```
第三方控件取名 custom
  

 
### 自定义ViewGroup

ViewGroup 的存在就是对子 View 进行管理，为其子 View 添加显示，响应的规划，因此自定义 ViewGroup 通常需要重写 onMeasure() 方法来对子 View 进行测量，重写 Layout() 方法来确定子 View 的位置，重写 onTouchEvent() 方法来增加响应事件。



### 事件拦截截止分析

由于 Android 中的 View 是树形结构，一层套一层，所以一旦点击某个 View 那这个点击事件，到底属于谁呢？子 View 和 父 ViewGroup 都有可能想要拦截这个事件进行处理。

在这里简述一下，肯定是由最高级的 ViewGroup 接收到事件，然后分发给下面，一层一层的。最下面的 View 接收到了事件，它会处理，也会一层一层的返回给上一级。