## 一 . 引入

由于 View 动画只能针对 View 来进行操作，类似于 TextView，Button，Layout 以及自定义的 View 这些 View 对象。一旦我们需要对一些非 View 对象进行操作， View 动画就不能满足了。另外一点就是 View 动画的并不能真正改变 View 的一些属性，而只是改变了其显示效果而已。比如点击 Button 的例子。

## 二. ObjectAnimator

该类使我们属性动画中最常接触的类，利用 ObjectAnimator 我们可以完成一些酷炫的动画效果，并且他可以对任意对象的任意属性来进行操作，添加动画效果。实际上 ObjectAnimator 是继承自 ValueAnimator 的，下一节，可以看看 ValueAnimator 的基本用法。我们现在来看一个简单的用法：

``` java
// textview 5 秒内变成透明，然后再全显示
ObjectAnimator animator = ObjectAnimator.ofFloat(textview, "alpha", 1f, 0f, 1f);  
// 持续时间
animator.setDuration(5000);  
animator.start(); 
// animator 具有使动画重复得方法
```

其他的效果都是类似的，具体的实现效果如下：

![](http://7xrl8j.com1.z0.glb.clouddn.com/PropetyAnimation.gif?imageMogr2/thumbnail/600x800)

而对于：

``` java
ObjectAnimator.ofFloat(textview, "alpha", 1f, 0f);
```

起作用就是 ObjectAnimator 不断的改变 tv 对象中的 alpha 属性的值。而实际上 textview 以及其父类都没有这个属性。那怎么去形成 alpha 的效果呢？实际上 ObjectAnimator 只需要去寻找 alpha 属性所对应的 set 和 get 方法即可。

以上机制同理在其他各种属性动画效果。

## 三. ValueAnimator

它是属性动画中比较重要的一个类。属性动画的核心就是数值的不断变化，而 ValueAnimator 就是起到这个变化数值的作用。我们制定初始值和结束值，并且指定运行时长，ValueAnimator 自然就会帮我们搞定一切。

``` java
ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);  
anim.setDuration(300);  
anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
    @Override  
    public void onAnimationUpdate(ValueAnimator animation) {  
        float currentValue = (float) animation.getAnimatedValue();  
        Log.d("TAG", "cuurent value is " + currentValue);  
    }  
});  
// anim 还有许多可以设置重复动画的 api
anim.start();
```

如上这个实例就会不断打印出 currentValue 从 0 到 1 的变化过程中数值的变化。其基本实现原理就是动画执行过程中不断的回调 addUpdateListener() 这个方法，而我们只需要在回调方法中取出这个数值的变化即可。ValueAnimator 中最常用的就是 ofFloat 和 ofInt 这两个方法了，另外还有一个是 ofObject 方法。

## 四. Animator 监听器

Animator 内部提供了一些监听器，用来帮助我们监听动画开始，结束以及重复的时间节点。而在此时，我们可以去添加一些逻辑事件。借助于底下这种方法，我们可以重写自己想重写的方法：

``` java
anim.addListener(new AnimatorListenerAdapter() {  
    @Override  
    public void onAnimationEnd(Animator animation) {  
    }  
}); 
```



## 五. Interpolator 的用法

Interpolator 中文名翻译过来成为插值器，现在取代的是一个现代化的接口 TimeInterpolator,系统预置的实现的插值器有以下几种。其作用是根据时间流逝的百分比来计算出当前属性值改变的百分比。

* LinerInterpolator:线性插值器，是匀速动画
* AccelerateDeceleateInterpolator:加速减速插值器，中间块，两头慢
* DeceleateInterpolator：减速插值器，动画越来越慢

TypeEvalutor 中文翻译成类型估值法，它的作用是根据当前属性改变的百分比来计算改变后的属性值，系统预置的有以下几种：

* IntEvaluator:针对整形属性
* FloatEvalutor:针对浮点型属性
* ArgbEvaluator:针对 color 属性

## 六. 总结和扩展阅读