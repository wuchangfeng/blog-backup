---
title: Arts-Development-of-Android-3-2
toc: true
categories: 读书笔记
description:
tags: Android
feature:
---

本篇参照郭神 Blog 中的实例，来分析 ViewGroup 的事件分发机制。

<!--more-->

## 引言

ViewGroup 中的点击事件是先到 view 还是先到 ViewGroup 呢？如果是 ViewGroup 的话，如何分发给指定的 View 呢？

## 一.实例

MainActivity 中代码如下所示：

``` java
mMyLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TAG", "myLayout on touch");
                return false;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "You clicked button1");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "You clicked button2");
            }
        });
```

自定义 MyLayout如下所示：

``` java
public class MyLayout extends LinearLayout {

    public MyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //@Override
    //public boolean onInterceptTouchEvent(MotionEvent event) {
    //    return true;
    //}
}
```

如上代码，我们点击 btn1 ,btn2 和空白区域打印出如下 log：

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f7gqhamsryj20o602ata6.jpg)

如上结果，我们点击 btn 的时候，注册的 onTouch 方法并没有执行，而是 onClick 将事件消费了，那么事件序列是先传递到 view 的吗？先等等。

而我们将**注释去掉**，则结果为：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f7gqhsymf7j20o602bta9.jpg)

发现无论点击空白区域还是 btn 都会调用 touch 方法。这就否定了上面的猜想了，所以根本上事件序列**还是先传递到 ViewGroup**再传递到 view 的。

## 二. 源码解析

``` java
public boolean dispatchTouchEvent(MotionEvent ev) {  
    final int action = ev.getAction();  
    final float xf = ev.getX();  
    final float yf = ev.getY();  
    final float scrolledXFloat = xf + mScrollX;  
    final float scrolledYFloat = yf + mScrollY;  
    final Rect frame = mTempRect;  
    boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;  
    if (action == MotionEvent.ACTION_DOWN) {  
        if (mMotionTarget != null) {  
            mMotionTarget = null;  
        }  
      	// 注释一
        if (disallowIntercept || !onInterceptTouchEvent(ev)) {  
            ev.setAction(MotionEvent.ACTION_DOWN);  
            final int scrolledXInt = (int) scrolledXFloat;  
            final int scrolledYInt = (int) scrolledYFloat;  
            final View[] children = mChildren;  
            final int count = mChildrenCount;  
            for (int i = count - 1; i >= 0; i--) {  
                final View child = children[i];  
                if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE  
                        || child.getAnimation() != null) {  
                    child.getHitRect(frame);  
                  	// 注释二
                    if (frame.contains(scrolledXInt, scrolledYInt)) {  
                        final float xc = scrolledXFloat - child.mLeft;  
                        final float yc = scrolledYFloat - child.mTop;  
                        ev.setLocation(xc, yc);  
                        child.mPrivateFlags &= ~CANCEL_NEXT_UP_EVENT;  
                        if (child.dispatchTouchEvent(ev))  {  
                            mMotionTarget = child;  
                            return true;  
                        }  
                    }  
                }  
            }  
        }  
    }  
```

在注释一处，我们关注一下 !onInterceptTouchEvent(ev)，该数值就是我们之前复写的。默认的为 false。取反则为真，满足该条件之后，进入 if 内，遍历 VIewGroup 中的所有 View。注释二处，判断当前的 view 是不是点击的 View 是的话，进入其内部，进行 view 的事件分发啦！

注意一点如何判断子元素是否能够接受到点击事件，看代码也能知道：子元素是否在播动画和点击事件的坐标是否在子元素的区域内。

另外 dispatchTouchEvent(ev) 是有返回值的，如果返回 ture 则会导致下面的方法都不会执行啦，这样就验证了我们之前的打印结果，btn 消费了事件，而 mylayout 的 touch 方法得不到执行。

``` java
    boolean isUpOrCancel = (action == MotionEvent.ACTION_UP) ||  
            (action == MotionEvent.ACTION_CANCEL);  
    if (isUpOrCancel) {  
        mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;  
    }  
    final View target = mMotionTarget;  
    if (target == null) {  
        ev.setLocation(xf, yf);  
        if ((mPrivateFlags & CANCEL_NEXT_UP_EVENT) != 0) {  
            ev.setAction(MotionEvent.ACTION_CANCEL);  
            mPrivateFlags &= ~CANCEL_NEXT_UP_EVENT;  
        }  
        return super.dispatchTouchEvent(ev);  
    }  
    if (!disallowIntercept && onInterceptTouchEvent(ev)) {  
        final float xc = scrolledXFloat - (float) target.mLeft;  
        final float yc = scrolledYFloat - (float) target.mTop;  
        mPrivateFlags &= ~CANCEL_NEXT_UP_EVENT;  
        ev.setAction(MotionEvent.ACTION_CANCEL);  
        ev.setLocation(xc, yc);  
        if (!target.dispatchTouchEvent(ev)) {  
        }  
        mMotionTarget = null;  
        return true;  
    }  
    if (isUpOrCancel) {  
        mMotionTarget = null;  
    }  
    final float xc = scrolledXFloat - (float) target.mLeft;  
    final float yc = scrolledYFloat - (float) target.mTop;  
    ev.setLocation(xc, yc);  
    if ((target.mPrivateFlags & CANCEL_NEXT_UP_EVENT) != 0) {  
        ev.setAction(MotionEvent.ACTION_CANCEL);  
        target.mPrivateFlags &= ~CANCEL_NEXT_UP_EVENT;  
        mMotionTarget = null;  
    }  
    return target.dispatchTouchEvent(ev);  
} 
```

而如果我们点击的是空白区域即 mylayout，则会执行上一段代码。最终会辗转执行 view 的dispatchTouchEvent(ev) 方法，而此时 ontouch 方法会被执行。

## 三. 参考和扩展阅读

* 《Android 开发艺术探索》
* [关于事件分发机制你需要知道的一切](http://blog.csdn.net/guolin_blog/article/details/9153747)