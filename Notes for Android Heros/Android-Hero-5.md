---
title: Android-群英传读书笔记（5）
date: 2016-03-22 19:07:45
tags: android
categories: About Java
---

Android基础之Scroll解析

<!-- more -->

### 滑动效果

滑动效果其实实现很简单，就是不断的改变 View 的位置，因此要实现滑动效果就要监听用户的触摸事件，并且根据事件的传入坐标，动态且不断的改变视图的位置。

* Android 坐标系

自然的我们将屏幕左上角作为坐标原点，X 轴向右为坐标 X 正方向， Y 轴向下为 Y 轴正方向。

在此坐标系中通过 getRawX(),getRawY()来获得 Android 坐标系中的坐标。

* 视图坐标系

以父视图左上角为坐标原点，通过 getX(),getY()来获取坐标。

* 触控事件MotionEvent

很重要，代码模板如下：


``` java
@override
public boolean onTOuchEvent(MotionEvent event){
		//获取当前输入点的X，Y坐标(视图坐标)
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				//处理按下
				break;
			case MotionEvent.ACTION_MOVE:
				//处理移动
				break;
			case MotionEvent.ACTION_UP:
				//处理离开
				break;
		}	
}	
```

	

* View 提供的获取坐标的方法
	
	* getTop():获取的是 View 自身的顶边到其父布局顶边的距离。 

    * getLeft():获取的是 View 自身的左边到其父布局左边的距离。
    
	* getRight():获取的是 View 自身的右边到其父布局右边的距离。

	* getBottom():获取的是 View 自身的底边到其父布局di边的距离。

* MotionEvent 提供的方法 
    
	* getX()，getY():即视图坐标。
	
	* getRawX(),getRawY():即绝对坐标。


实现滑动的几种方法


### Layout方法

这种方法很简单，改变 Layout 的位置，代码如下：

``` java
@override
public boolean onTouchEvent(MotionEvent ev){
		int rawX = (int) (event.getRawX());
		int rawY = (int) (event.getRawY());
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				//记录触摸点的坐标
				lastX = rawX;
				lastY = rawY;
				break;
			case MotionEvent.ACTION_MOVE:
				//计算偏移量
				int offestX = rawX-lastX;
				int offestY = rawY-lastY;
				//在当前left,right,top,bottom基础上加上偏移量
				layout(getLeft()+offestX,
					getTop()+offestY,
					getRight()+offestX,
					getBottom()+offestY);
				//重新设置初始坐标
				lastX = rawX；
				lastY = rawY；
				break;
		}
}
```

**每次执行完Action_Move**之后，一定要重新设置初始坐标，这样才能准确获取偏移量。

### scrollTo()与scrollBy()

两者之间区别应该很简单，一个直接到达某位置，一个偏移多少距离。

注意一点十分重要：如果 View 移动，移动的只是它的内容即 Content。所以我们要想使 View 移动，则需要移动它的 ViewGroup.

``` java
((View).getParent()).scrollBy(-offestX,-offestY);
```

另外由于我们移动的是 ViewGroup，子 View 并没有移动，但是子 View 左边表现的效果经常为反方向，所以偏移量我们要取负值，即相反方向。


### Scroller

该类能实现平滑移动的效果，而不是瞬间完成的。本质上跟 scrollBy 和 scrollTo 是一样的效果，只不过 Scroller 在 ACTION_MOVE 事件中不断获取手机移动的微小距离，这样就讲一段距离划分成了N个小偏移量。

使用 Scroller 具有以下三个步骤：

* 初始化 Scroller

通过他的构造方法来创建一个 Scroller 对象，如下：

``` java
mScroll = new Scroller(context);
```



* 重写 computeScroll()方法，实现模拟滑动

computeScroll()方法，是 Scroller 的核心，系统在绘制 View 的时候会在 draw() 方法中调用该方法。实际上是 scrollTo() 方法。再结合 Scroller 的值获取当前的滚动值。通过不断移动一个小的距离来实现整体上的平滑移动。通常情况下代码模板如下：

``` java
@override
public void computeScroll(){
		super.computScroll();
        //判断Scroller是否执行完毕
		if(mScroller.computeScrollOffest()){
      		((View)getParent()).scrollTo(
 			    mScroller.getCurrX(),
				mScroller.getCurrY();
				
			//通过重绘来不断调用computeScroll
			invalidate();
		 )
		}
}
```

Scroller 类提供了 computeScrollOffest() 方法来判断是否完成了整个滑动，同时提供了 getCurrX(),getCurrY()来获得当前的滑动坐标。

invalidate()方法。因为 computeScroll() 方法不会自动调用，只能通过 invalidate()-draw()-computeScroll() 来间接调用 computeScroll() ，故需要在模板代码中调用 invalidate() 方法，实现循环获取 scrollX() 和 scrollY() 的目的。当模拟过程结束后，scroller.computeScrollOffest() 方法会返回 false(),从而中断循环，完成整个平滑移动过程。

* startScroll 开启平滑移动过程

startScroll()方法具有两个重载方法：
  
``` java
public void startScroll(int startX,int startY,int dx,int dy,int duration)
public void startScroll(int startX,int startY,int dx,int dy)
```

很容易理解上述过程。


* 实例学习

构造方法中，初始化 Scroller 对象，并重写 View 的 computeScroll()方法，最后需要监听**手指离开屏幕**的事件，并在该事件中通过调用startScroll() 方法完成平滑移动。监听手指离开屏幕的事件，只需要在 onTouchEvent 中增加一个 ACTION_UP 监听即可，代码如下：

``` java
case MotionEvent.ACTION_UP:
		//手指离开，开始滑动过程
		View viewgroup = ((View)getParent());
        mScroller.startScroll(
           viewgroup.getScrollX(),
			viewgroup.getScrollY(),
			-viewGroup.getScrollX(),
			-viewgroup.getScrollY();
        );
		invalidate();
		break;
```

注意偏移量要取其相反数。


### ViewDragerHelper

通过 ViewDragerHelper 基本上可以实现各种不同的滑动，拖放需求。下面一个实例，演示如何实现滑动布局，类似于 QQ 侧边栏。

* 初始化 ViewDragHelper

 初始化 ViewDragHelper ，其通常定义在一个 ViewGroup 的内部，通过静态工厂的方法进行初始化，代码：

``` java
mViewDragHelper = ViewDragHelper.create(this,callback); 
```

其第一个参数是要监听的 View ，通常为一个 ViewGroup ，即 parentView;第二个参数是一个 Callback 回调，这个回调是整个 ViewDragHelper 的核心。


* 拦截事件

重写事件拦截方法，将事件传递给 ViewDragHelper 进行处理：

``` java
@override

public boolean onInterceptTouchEvent(MotionEvent ev){
		return mViewDragHelper.shouldInterceptTouchEvent(ev);
}
    
@override

public boolean onTouchEvent(MotionEvent event){
		//将触摸事件传递给ViewDragHelper，此操作必不可少
		mViewDragHelper.processTouchEvent(event);
		return true;
}
```

上面就是典型的 Android 事件机制

* 处理computeScroll()

因为 ViewDragHelper 内部也是通过 Scroller 来实现平滑移动的，所以也需要重写 computeScroll()方法。通常可以使用模板代码如下：

``` java
@override
    public void computeScroll(){
		if(mViewDragHelper.continueSettling(true)){
			ViewCompat.postinvalidateOnAnimation(this);
		}

}
```

* 处理回调CallBack

关键的 callback 实现：

``` java
private ViewDragHelper.Callback callback = new ViewDragHelper.CallBack(){
		@override
		public boolean tryCaptureView(View child,int pointerId){
			return false;
	}
}
```

上面的tryCaptureView(),通过这个，可以指定在创建 ViewDragHelper 时，参数 parentView 中的哪一个子 view 可以被移动。例如在本例中 ViewGroup 中有两个 View 即
MenuView,MainView，如下指定时，则只有 MainView 可以被移动：

``` java
	@override
	public boolean tryCaptureView(View child ,int pointerId){
		//如果当前触摸的child是mMainView时开始检测
		return mMainView == child；
	}
```

具体的滑动方法为：clampViewPostionVertical() 和 clampViewPostionHorizontal(),如果要是实现滑动效果,那这两个方法是必须要重写的，因为他默认是返回0的。

重写以上三个方法，基本上就可以实现一个基本的滑动效果了：

``` java
private ViewDraghelper.Callback callback = 

	new ViewDragHelper.Callback(){

	@override
	public boolean tryCaptureView(View child ,int pointerId){
		//如果当前触摸的child是mMainView时开始检测
		return mMainView == child；
	}

	@override
	public int clampViewPostionVertical(View child,int top,int dy){
        //垂直方向不滑动
		return 0;
	}

	@override
	public int clampViewPostionHorizontal(View child,int left,int dx){
		//水平滑动
		return left;
	}
	
}
``` 

继续优化。 除了 Scroller 可以实现手指离开屏幕之后的事件，这里的 ViewDragHelper 也可以实现这个效果，其中的 ViewDragHelper.callback 中，提供了-onViewReleased() ，通过重写其，可以非常简单实现**手指离开屏幕的**操作，其实其内部也是通过 Scroller 来实现的，这也是**前面重写** computeScroll() 方法的原因，代码如下：

``` java
// 拖动结束之后调用
@override
public void onViewReleased(View releasedChild,float xvel,float yvel){
	super.onViewRelesed(releasedChild,xvel,yvel);
	// 手指抬起后缓慢移动到指定的位置
	if(mMainView.getLeft()<500){
			// 关闭菜单，相当于Scroller中的startscroll()方法
			mViewDragHelper.smoothSlideViewTo(mMainView,0,0);
			ViewCompat.postInvalidateOnAnimation(DragViewGroup,this);}
	else{
			// 打开菜单		
 			mViewDragHelper.smoothSlideViewTo(mMainView,300,0);
			ViewCompat.postInvalidateOnAnimation(DragViewGroup,this);		
		}
		
	}
}
```

基本过程都完成了，现在自定义一个 ViewGroup 完成整个实例编写。在自定义的 ViewGroup 的 onFinishInflate()方法中，按照顺序将子 View分别定义成 MainView 和 MenuView，并在 onSizeChanged() 方法中获得 View 的宽度。即需要根据 View 的宽度来处理滑动后的效果，可以使用这个值进行判断：

``` java
// 加载完成布局后调用
	@override
	protected void onFinsihInflate(){
		super.onFinishInflate();
		mMenuView = getChildAt(0);
		mMainView = getChildAt(1);

	}
	@override
	pritected void onSizeChanged(int w,int h,int oldw，int oldh){
		super.onSizeChanged(w,h,oldh,oldw);
		mWidth = mMenuView.getMeasuredWidth();
}
```

上述简单实现了 QQ 侧滑。实际上在 ViewDragHelper.CallBack 中，系统定义了大量监听帮助我们处理各种监听的事件：

* onViewCaptured() 用户触摸到 View 后回调
* onViewDragStateChanged() 拖拽状态改变时回调
* onViewPostionChanged() 位置改变时进行回调，常用语滑动时更改 Scale 进行缩放。