### Before

* Ubuntu 15.04 系统 
* Python 2.7
* numpy、cython、matplotlib、scipy
* 推荐教程：https://pythonprogramming.net/matplotlib-intro-tutorial/

### Introduction to Matplotlib and basic line

``` python
import matplotlib.pyplot as plt
# 对应横纵 X/Y 轴坐标
plt.plot([1,2,3],[5,7,4])
plt.show()
```

[1,2,3] 对应的是 X 轴坐标，[5,7,4] 对应的是 Y 轴坐标。截图如下：

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f927ro4cmaj20sa0m5ahk.jpg)

当然只指定 Y 的坐标也是可以的，plot() 会默认其为 Y 轴坐标，并自动给 X 轴从 0 开始起坐标，示意截图如下：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f927ssbfivj20sa0m545r.jpg)

下面这个实例可以显示坐标以点的形式分布于区域中，你可以验证一下去掉 'ro',会发现其将以折线图形式存在于图中：

``` python
import matplotlib.pyplot as plt
# 指定对应的 X，Y坐标，ro 属性为指定点
plt.plot([1,2,3,4], [1,4,9,16], 'ro')
# 指定 X 和 Y 的范围
plt.axis([0, 6, 0, 20])
plt.show()
```

截图如下：

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f9280c9hybj20no0m5gr2.jpg)

### Legends, Titles, and Labels with Matplotlib

这一节学习两条对比的折线图，给折线图添加 Label 以及 Legend(也就是下图右上角的两个对比示意)

``` python
import matplotlib.pyplot as plt
# 对应的 X/Y 坐标
x = [1,2,3]
y = [5,7,4]
# 对应的 X/Y 坐标
x2 = [1,2,3]
y2 = [10,14,12]
```

绘制每一条折线，并添加对应 Label：

``` python
plt.plot(x, y, label='First Line')
plt.plot(x2, y2, label='Second Line')
```

绘图完毕之后，接着添加一些属性：

``` python
# 给 X 和 Y 轴添上名字
plt.xlabel('Plot Number')
plt.ylabel('Important var')
# 图的标题
plt.title('Interesting Graph\nCheck it out')
# 添加 label 到右上角
plt.legend()
plt.show()
```

对应的示意图如下所示：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f9285wsizoj20sa0m5aha.jpg)



好了，以上就是我跟着教程学习的前两章内容笔记。