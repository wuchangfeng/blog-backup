### Bar Charts and Histograms with Matplotlib

这一节学习柱状图直方图的表示。

> 直方图与条形图的区别在于，直方图是用面积而非高度来表示数量。直方图由一组矩形组成，每一个矩形的面积表示在相应的区间中样本百分数。每个矩形的高度表示样本密度，即区间中样本百分数除以该区间长度（或称矩形宽度）。其面积为百分数，总面积为100%。直方图下两个数值之间的面积给出了落在那个区间内样本百分数

``` python
import matplotlib.pyplot as plt
# 条目一，分别对应着直方图的 X 和 Y 的坐标
plt.bar([1,3,5,7,9],[5,2,7,8,2], label="Example one")
# 条目二，同上
plt.bar([2,4,6,8,10],[8,6,2,5,6], label="Example two", color='g')
plt.legend()
# X Y 轴的标示
plt.xlabel('bar number')
plt.ylabel('bar height')
plt.title('Epic Graph\nAnother Line! Whoa')
plt.show()
```

文档对于上述代码的解释，无非是颜色的选取

> The plt.bar creates the bar chart for us. If you do not explicitly choose a color, then, despite doing multiple plots, all bars will look the same. This gives us a change to cover a new Matplotlib customization option, however. You can use color to color just about any kind of plot, using colors like g for green, b for blue, r for red, and so on. You can also use hex color codes, like #191970

示意图效果如下：

![](http://ww4.sinaimg.cn/large/b10d1ea5jw1f92932e8thj20sa0m57dn.jpg)

现在我们来试一下直方图，直方图的定义在文章开始时候已经给了说明，看代码就好：

``` Python
import matplotlib.pyplot as plt
# 年龄分布
population_ages = [22,55,62,45,21,22,34,42,42,4,99,102,110,120,121,122,130,111,115,112,80,75,65,54,44,43,42,48]
# 分布区间
bins = [0,10,20,30,40,50,60,70,80,90,100,110,120,130]
# 直方图的绘制
plt.hist(population_ages, bins, histtype='bar', rwidth=0.8)

plt.xlabel('x')
plt.ylabel('y')
plt.title('Interesting Graph\nCheck it out')
plt.legend()
plt.show()
```

如下为绘制结果，从图上来看，这里直方图还只是将同一个年龄段的放置在一起，并不是传统意义上的直方图，不过也能满足一定的需求：

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f92947ofj2j20sa0m547l.jpg)

### Scatter Plots with Matplotlib

散点图的绘制。散点图的作用通常是比较两个或者三个变量，应该还是挺重要的，比如看点的分布区域，在 y = x 的上方多还是下方多，这都是很有意义的。

``` python
import matplotlib.pyplot as plt

x = [1,2,3,4,5,6,7,8]
y = [5,2,4,2,1,4,5,2]
# color，size,marker 都可以自定义
plt.scatter(x,y, label='skitscat', color='k', s=25, marker="o")
# x y 轴名称
plt.xlabel('x')
plt.ylabel('y')
plt.title('Interesting Graph\nCheck it out')
plt.legend()
plt.show()
```

示意截图如下：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f929i4ceunj20sa0m5th8.jpg)

好了，这是学习第三第四节的内容。