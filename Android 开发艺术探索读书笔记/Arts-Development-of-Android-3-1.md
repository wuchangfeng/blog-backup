---
title: Arts-Development-of-Android-3-1
toc: true
categories: 读书笔记
description:
tags: Android
feature:
---

本章介绍 View 的基本概念和 View 的滑动相关知识以及关于 View 的事件分发机制实例和源码解析。

<!--more-->

## 一. View 的基础

### 1.1. View 的位置参数

View 的位置主要由四个参数来标示(**相对坐标，相对于 View 的父容器**)：

1. top 标示左上角纵坐标
2. left 标示左上角横坐标 
3. right 标示右下角横坐标
4. bottom 标示右下角纵坐标

而关于 View 的坐标，可以参见底下这张图，[图片出处](http://blog.csdn.net/jason0539/article/details/42743531)

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f7gm1dn0uaj20ti11iq6n.jpg)

### 1.2 MotionEvent 和 TouchSlop

#### 1.2.1 MotionEvent

- ACTION_DOWN:手指刚接触屏幕时候
- ACTION_MOVE:手指在屏幕上移动
- ACTION_UP:手指松开一瞬间

典型的事件序列有以下两种情况：

- 点击屏幕后离开，事件序列为： DOWN-UP
- 点击屏幕滑动一会，再松开，事件序列为：DOWN-MOVE...-UP

而通过点击事件中的 MotionEvent 对象我们可以获得点击事件的坐标：

- getX/getY:当前 VIew 的左上角的 x 和 y 坐标
- getRawX/getRawY:返回相对屏幕左上角的 x和 y 坐标

#### 1.2.2 TouchSlop

TouchSlop 即为系统所能识别的最小的滑动距离，即超过此距离，系统认为你滑动了。如下即可获取该值：

```java
ViewConfiguration.get(getContext()).getScaledTouchSlop()
```

### 1.3 VelocityTracker,GestureDeteCtor 和 Scroller

- VelocityTracker 用于追踪手指在滑动过程中的速度
- GestureDeteCtor 用于辅助检测用户的单击，滑动，长按，双击等行为
- Scroller 用于实现 View **弹性滑动的对象**,不同于 scrollTo/scrollBy 的瞬间完成，详细内容见下一节 View 的滑动

## 二. View 的滑动

### 2.1 使用 scrollTo/scrollBy

ScrollBy 实际上也是调用了 ScrollTo 方法，它实现了基于位置的相对滑动，而 ScrollTo 则实现了基于所传递参数的绝对滑动。使用 ScrollBy 和 ScrollTo 只能实现 View 的内容移动而 View 的本身则不能移动。

### 2.2 使用动画

- 采用视图动画，一般是设置 XML 属性
- 采用属性动画，即 ObjectAnimator 

**注意上面的视图动画并不能正真的改变 View 的位置，即带来的后果是移动过后的 View 无法响应 onClick 事件**，而属性动画可以解决这个问题。详细的可以参见第七章。

### 2.3 改变布局参数

这种方式很好理解，我们可以直接来看代码是如何实现的：

```java
MarginLayoutParams params = (MarginLayoutParams) mButton.getLayoutParams();
params.width += 100;
params.leftMargin += 100;
mButton.setLayoutParams(params);
```



## 三. 弹性滑动

弹性滑动共同的思想都是将一次大的滑动分割成若干次小的滑动，并且在指定时间内完成。

### 3.1 使用 Scroller

Scroller 的实现原理，可以去参见[关于 Scroller 你需要知道的一切](http://blog.csdn.net/guolin_blog/article/details/48719871)

scroller 本身并不能实现 View 的滑动，需要结合 View 的 computeScroll 方法才能完成弹性滑动，它不断让View **不断重新绘制**,而每一次重新绘制都会距离滑动起始时间有一个时间间隔，通过这个时间间隔 Scroller 就可以得出 View 当前的滑动位置，知道了**滑动位置**,就可以通过 scrollTo 方法来完成 View 的滑动。多次滑动就会导致 View 的滑动。整个过程对 View 没有丝毫的引用。

### 3.2 通过动画

```java
 final ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(2000);
  // 动画添加监听器
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          // 获取动画完成比例值
            float fraction = animator.getAnimatedFraction();
          // 根据比例值对目标view进行滑动
            targetView.scrollTo(startX +(int)(deltax * fraction), 0);
        }
    });
   // 开始执行
    animator.start();
```

本质上还是给人家一个动画效果，实际上还是通过 scrollTo() 方法来完成的。书上说这里的滑动针对的 View 的内容而非 View 本身，这个还需要实际验证一下。

### 3.3 使用延时策略

核心思想就是通过发送一系列的延时消息从而达到一种渐进式的效果，具体来说可以是 Handler 或者 View 的 postDelayed 方法。

## 四. View 的事件分发机制

整个过程就是对 MotionEvent 的事件分发过程。

- public boolean dispatchTouchEvent(MotionEvent ev) 

  用来对事件进行分发。如果事件能够传递给当前的 View ，那么此方法一定会被调用，返回的结果受**当前View** 的 onTouchEvent 和下级 View 的 dispatchTouchEvent 方法的影响，表示 **是否消耗当前的事件**

- public boolean onInterceptTouchEvent(MotionEvent event) 

  在 **上述方法内部**进行调用,用来判断是否拦截某个事件。如果当前 View 拦截了某个事件，那么在同一事件序列中，此方法不会再次被调用，返回结果表示是否拦截当前的事件

- public boolean onTouchEvent(MotionEvent event)

   在 dispatchTouchEvent 中调用，用来处理点击事件

可以用如下伪代码来标示上述三个方法之间的区别和关系：

```java
public boolean dispatchTouchEvent(MotionEvent ev){
  boolean cunsume = false;
  if(onInterceptTouchEvent(ev)){
  	consume = onTouchEvent(ev);
} else {
  	consume = child.dispatchTouchEvent(ev)
  }
  return consume;
}
```

总结的一些常见事件概念：

- 同一事件序列是指从手指接触屏幕那一时刻开始起，直到离开为止
- 正常情况下，一个事件序列只能被一个 View 拦截并且消耗。因为一旦一个元素拦截了某件事件，那么同一个事件序列内的所有事件都会交给它处理，不能分别由两个序列同时处理。
- 某个 View 一旦决定拦截，那么这一个事件序列都只能由他来处理，并且它的 onINterceptTouchEvent 不会再被调用，即不用再去询问该 View 是否还要拦截事件序列了。
- 某个 View 一旦开始处理事件，如果它不消耗 ACTION_DOWN 事件(onTouchEvent 返回了 false)，那么同一个时间序列其他的事件也不会交给它处理，并将事件重新交给它的父元素去处理，即父元素的 onTouchEvent 会被调用。


- ViewGroup 默认**不拦截** 任何事件序列。Android 源码中 ViewGroup 的 onInterceptTouchEvent 方法默认返回 false
- View  **没有 onInterceptTouchEvent** 方法。一旦有事件传递给它，那么它的 onTouchEvent 方法就会被调用
- View 的 onTouchEvent 默认都会消耗事件(返回 true)。除非其为不可点击的(短和长同时都是不可点击的)​

事件分发机制过程解析：

1. Activity 对事件的分发过程
2. 顶级 View 对事件分发的过程
3. View 对点击事件的处理过程

## 五. 事件分发机制实例

先来看一个实例代码：

```java
  btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick execute");
            }
        });

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAG", "onTouch execute, action " + event.getAction());
                return false;
            }
        });
```

如上，即给一个 btn 设置了一个 onClick 和 onTouch 事件。我们点击之后运行，Log 如下所示：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f7gnoi6xluj20mm02hwfv.jpg)

可以发现 Touch 的优先级是高于 Click 的。onTouch 执行了两次，一个是 ACTION_DOWN 一次是 ACTION_UP.而我们将上述代码中的 return false 改成 return ture，则执行结果如下：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f7gnqo442cj20my01lt9h.jpg)

发现并没有执行 onclick 方法，即该事件序列被 onTouch 给**消费掉了。**

我们来看一下源码(View 对点击事件的处理)：

```java
public boolean dispatchTouchEvent(MotionEvent event) {  
    if (mOnTouchListener != null && (mViewFlags & ENABLED_MASK) == ENABLED &&  
            mOnTouchListener.onTouch(this, event)) {  
        return true;  
    }  
    return onTouchEvent(event);  
} 
```

因为 View 是单独的元素，所以它无法继续传递事件了，所以只能自己处理。首先判断有没有设置 onTouchListener,如果其 onTouch 返回 ture，则无法调用 onTouchEvent 了。可见 onTouchListener 的优先级高于 onTouchEvent 。

从源码中可以看出，onTouch 和 onTouchEvent 这两个方法都是在 View 的 dispatchTouchEvent 中调用的，onTouch 优先于 onTouchEvent 执行。如果在 onTouch 方法中通过返回 true 将事件消费掉，onTouchEvent 将不会再执行。

另外需要注意的是，onTouch 能够得到执行需要两个前提条件，第一 mOnTouchListener 的值不能为空，第二当前点击的控件必须是 enable 的。**因此如果你有一个控件是非 enable 的，那么给它注册 onTouch 事件将永远得不到执行**。对于这一类控件，如果我们想要监听它的 touch 事件，就必须通过在该控件中重写 onTouchEvent 方法来实现。

结合前面的例子来分析一下了，首先在 dispatchTouchEvent 中最先执行的就是 onTouch 方法，因此 onTouch 肯定是要优先于 onClick 执行的，也是印证了刚刚的打印结果。而如果在 onTouch 方法里返回了 true，就会让dispatchTouchEvent 方法直接返回 true，不会再继续往下执行。而打印结果也证实了如果 onTouch 返回 true，onClick (onTouchEvent) 就不会再执行了。

我们继续看看 onTouchEvent() ,验证一下其内部是否调用了 onClick() 方法：

```java
public boolean onTouchEvent(MotionEvent event) {  
    final int viewFlags = mViewFlags;  
    if ((viewFlags & ENABLED_MASK) == DISABLED) {  
        // A disabled view that is clickable still consumes the touch  
        // events, it just doesn't respond to them.  
        return (((viewFlags & CLICKABLE) == CLICKABLE ||  
                (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE));  
    }  
    if (mTouchDelegate != null) {  
        if (mTouchDelegate.onTouchEvent(event)) {  
            return true;  
        }  
    }  
    if (((viewFlags & CLICKABLE) == CLICKABLE ||  
            (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)) {  
        switch (event.getAction()) {  
            case MotionEvent.ACTION_UP:  
                //...
            	performClick(); 
            	//...
            case MotionEvent.ACTION_DOWN:  
                //...  
            case MotionEvent.ACTION_CANCEL:  
               //... 
            case MotionEvent.ACTION_MOVE:  
               //...
        }  
        return true;  
    }  
    return false;  
} 
```

从上面的源码中看只需要一个 View 的 CLICKABLE 和 LONG_ABLE 的其中一个为 true，该 View 就会消耗该事件，即 onTouchEvent 返回 true。然后当 ACTION_UP 时候，其内部会调用 performClick():

```java
public boolean performClick() {  
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);  
    if (mOnClickListener != null) {  
        playSoundEffect(SoundEffectConstants.CLICK);  
        mOnClickListener.onClick(this);  // 调用 onClick()
        return true;  
    }  
    return false;  
}
```

VIew 的 LONG_CLICKABLE 默认为 true，而 CLICKABLE 要视具体的 View 而定。而点击一个默认不可被点击的 View 时候，其内部事件序列只会到 ACTION_DOWN,后续的都不会再执行了。

## 六. 参考和扩展阅读

* 《Android 开发艺术探索》
* [Android 事件分发机制完全解析](http://blog.csdn.net/guolin_blog/article/details/9097463)