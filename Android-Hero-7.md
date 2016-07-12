---
title: Android-群英传读书笔记（7）
date: 2016-03-16 09:02:52
tags: android
categories: About Java
---

Android动画机制与使用技巧

<!-- more -->

### 7.1 Android 视图动画
视图动画优点就是使用效率高并且很方便。
视图动画最大的缺点就是不具备交互性，当某个元素发生视图动画后，其响应事件还在原来的位置。



#### 7.11 透明度动画

``` java
AlphaAnimation aa = new AlphaAnimation(0,1);
aa.setDruation(1000);
view.startAnimation(aa);	
```

#### 7.12 旋转动画

``` java	
RotateAnimation ra = new RotateAnimation(0,360,100,100);
ra.setDuration(1000);
view.startAnimation(ra);	
```

上述代码参数为旋转的起始角度和旋转的中心点坐标。也可以通过设置参数来控制旋转动画的参考系，代码如下所示，其参考系为自身中心点。
	
``` java
RotateAnimation ra = new Rotate(0,360,RotateAnimator.RELATIVE_TO_SELF,0.5F,
RotateAnimation.RELATIVE_TO_SELF,0.5f);
```

#### 7.13 位移动画

``` java
TranslateAnimation ta = new TranslateAnimation(0,200,0,300);
ta.setDuration(1000);
view.startAnimation(ta);
```

#### 7.14 缩放动画
	
``` java
ScaleAnimation sa = new ScaleAnimation(0,2,0,2);
sa.setDuration(1000);
view.startAnimation(sa);
```
	
与旋转动画一样，缩放动画也可以设置缩放的中心点。

#### 7.15 动画集合

``` java
AnimationSet as = new Animation(true);
as.setDuration(1000);

AlphaAnimation aa = new AlphaAnimation(0,1);
aa.setDuration(1000);
as.AddAnimation(aa);

TranslateAnimation ta = new TranslateAnimation(0,100,0,200);
ta.setDuration(1000);
as.AddAnimation(ta);

view.startAnimation(as);
```

#### 补充

对于动画事件，Android 也添加了对应的监听回调，要添加相应的方法，代码如下所示：

``` java
animation.setAnimationListener(new Animation.AnimationListener(){
	@override
	public void OnAnimationStart(Animation animation){
		//通过这些回调监听，可以知道动画开始结束等事件。
	 }
	}
);
```	

### 7.2 Android 属性动画

在属性动画中使用最多的即AnimatorSet和ObjectAnimator配合。ObjectAnimator只能控制一个对象的一个属性，使用多个ObjectAnimator组合到AnimatorSet形成一个动画。ObjectAnimator能够自动驱动，调用setFrameDelay()设置动画之间的间隙时间，调整帧率，减少动画绘制过程中频繁绘制，不影响效果的前提下，减少CPU的内耗。最重要的是其真是的控制了一个View的属性值。

#### 7.21 ObjectAnimator

动画框架中最重要的实行类，创建一个ObjectAnimator只需通过其静态工厂方法直接返回一个ObjectAnimator对象。参数则有：一个对象和**对象的属性名字**，并且这个属性必须含有get和set参数，内部会通过Java反射机制来调用set函数修改对象属性值。setInterpolator设置相应插值器。

如下平移动画实现效果：

``` java
ObjectAnimator animator = ObjectAnimator.ofFloat(
		view,
		"translationX",//所谓属性？
        300);
		animator.setDuration(300);
		animator.start();
```

常用属性值：

* translationX 和 translationY:作为一种**增量**来控制**View**对象从它布局容器中左上角坐标偏移位置。
* rotation,rotationX 和 roationY：控制 View 对象围绕着支点进行2D，3D旋转。
* scaleX,scaleY:控制View对象围绕着他的支点进行 2D 缩放。
* pivotX 和 pivotY：控制着View对象的支点位置，围绕着这两个支点进行旋转和缩放变换处理，默认为中心点。
* x,y:描述 View 对象在容器中最终的位置。
* alpha:表示 View 对象的透明度。

如果一个属性没有set和get方法，我们可以通过自定义属性类或者包装类，来给属性简介增加get，set方法；或者是通过ValueAnimator来实现。

第一种方法：

``` java
private static class WrapperView{
	
	private View mTarget;
	
	public WrapperView(View target){
		mTarget=target;
	 }
	public int getWidth(){
		return mTarget.getLayoutParams().width;
	 }
	public void setWidth(int width){
		mTarget.getLayoutParams().width=width;
		mTarget.requestLayout();
	 }
}
```

通过以上方法，就给属性包装了一层，并且增加了get和set方法。使用时只需要直接操纵包装类就可以间接调用到get和set方法了，代码如下：

``` java
ViewWrapper wrapper = new ViewWrapper(mButton);
ObjectAnimator.ofInt(wrapper,"width",500).setDuration(5000).start();
```

#### 7.22 PropertyValuesHolder

#### 7.23 ValueAnimator

非常重要，甚至连ObjectAnimator都继承自它。
本身不提供任何动画效果，就像一个数值发生器，用来产生具有一定规则的数字，从而让调用者来控制动画的实现过程。一般用法如下所示：

``` java
ValueAnimator animator = ValueAnimator.ofInt(0,100);
	animator.setTarget(View);
	animator.setDuration(1000).start();
	animator.addUpdateListener(new AnimatorUpdateListener(){
		@override
		public void onAnimatorUpdate(VlaueAnimator animation){
			Float value=(Float)animator.getAnimatedValue();
			//TO use value	
		}
});
```

#### 7.24 动画事件的监听

完整动画具有：Start,Repeat,End,Cancel四个过程，通过Android提供的接口可以很方便的监听这四个事件。很多时候我们只关心onAnimationEnd事件，所以提供了一个AnimatorListenerAdapter让我们选择必要的事件进行监听，代码如下所示：
	
``` java
anim.addListener(new AnimatorListenerAdapter(){
		@override
		public void onAnimationEnd(Animator animation){
			//todo
		}
});
```

#### 7.25 AnimatorSet

对一个对象作用多种属性效果：

``` java
ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"translationX",300f);
ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,"scaleX",1f,0f,1f);
ObjectAnimator animator3 = ObjectAnimator.ofFloat(view,"scaleY",1f,0f,1f);
AnimatorSet set = new AnimatorSet();
set.setDuration(1000);
set.playTogether(animator1,animator2,animator3);
set.start();
```

#### 7.26 在XML中使用属性动画

#### 7.27 View中animate方法



#### 7.4 插值器(interpolators)

通过插值器，可以控制动画变化的频率，非常类似物理概念中的加速度。简单来讲，就像一个位移动画，如果使用线性插值器，那么在持续时间内，单位时间内所移动的距离都是一样的，如果使用加速度插值器，那么单位时间内的移动距离将越来越大。

#### 7.5 自定义动画

需要实现其 **applyTransformation** 的逻辑，不过通常情况下也要覆盖父类的 initialize 方法来实现一些初始化工作。applyTransformation 方法有如下所示的两个参数：

``` java
applyTransformation(float interpolatedTime,Transformation t);
```

第一参数就是插值器因子，去0.1到1.第二个参数为矩阵封装类，使用它获得当前的矩阵对象，代码如下：

``` java
final Matrix = t.getMatrix();
```
通过改变矩阵对象，可以将动画效果实现出来，而对于 matrix 的变换操作，基本上可以实现任何动画效果。

``` java
@override
protected void applyTransformation(float interpolatedTime,transformation t){
		final Matrix matrix = t.getMatrix();
		//通过矩阵来实现各种操作
		matrix.XXXX
}
```

模拟一个电视机的关机效果，非常简单，一个图片的纵向比例不断缩小即可。

``` java
	final Matrix matrix = t.getMatrix();
		//通过矩阵来实现各种操作
		matrix.preScale(1,1-interpolatedTime,mCenterWidth,mCenterHeight);
```

其中 mCenterWidth,mCenterHeight为图像缩放的中心点，设置为图片的中心点即可。


接下来结合矩阵，实现一个自定义的 3D 效果，借助于 Camera 类，其封装了 OpenGL 的 3D 动画，可以将 Camera 想象成一个真实的 摄像机，当物体固定在某处时，只要移动 摄像机就可以获得各种 3D 效果。

``` java
@override
public void initialize(int width,int height,int parentWidth,int parentHeight){
		super.initialize( width,height, parentWidth,parentHeight);
		//设置默认时长
		setDuration(2000);
		//动画结束后保留状态
		setFillAfter(true);
		//设置默认插值器
		setInterpolator(new BounceInterpolator);
		mCenterWidth = width/2;
		mCenterHeight = Height/2;
}
```

接下来自定义动画核心：
	
``` java
@override
	pritected void applyTransformation(float interpolatedTime,Transformation t){
		final Matrix matrix = t.getMatrix();
		mCamsera.save();
		//设置Camera的旋转角度
		mCamera.rotateY(mRotateY * interpolatedTime);
		//将旋转变化作用到 matrix 上
		mCamera.getMatrix(matrix);
		mCamera.restore();
		//通过pre方法设置矩阵作用前的偏移量来改变旋转中心
		matrix.preTranslate(mCenterWidth,mCenterHeight);
		matrix.postTranslate(-mCenterWidth,-mCenterHeight)
}
```

改变旋转时的默认旋转中心。






### 扩充阅读

[郭霖](http://blog.csdn.net/guolin_blog/article/details/43536355)