### Stack Plots with Matplotlib




在本节 Matplotlib 数据可视化教程中，我们将介绍如何创建堆栈图。堆栈图是要表明随着时间的推移，“从部分到整体”的变化过程。

让我们考虑的情况：我们有24小时一天，我们想看看我们是如何花出去的时间。将我们的活动分为：睡觉，吃饭，工作和玩耍。

``` python
import matplotlib.pyplot as plt

days = [1,2,3,4,5]
# 五天中各种时间的变化
sleeping = [7,8,6,11,7]
eating =   [2,3,4,3,2]
working =  [7,8,7,2,2]
playing =  [8,5,7,8,13]
# 一些属性的设置
plt.plot([],[],color='m', label='Sleeping', linewidth=5)
plt.plot([],[],color='c', label='Eating', linewidth=5)
plt.plot([],[],color='r', label='Working', linewidth=5)
plt.plot([],[],color='k', label='Playing', linewidth=5)
# 绘制
plt.stackplot(days, sleeping,eating,working,playing, colors=['m','c','r','k'])
#  x y title 的赋值
plt.xlabel('x')
plt.ylabel('y')
plt.title('Interesting Graph\nCheck it out')
plt.legend()
plt.show()
```

示意图如下所示：

![](http://ww2.sinaimg.cn/large/b10d1ea5jw1f92b89foxqj20qf0m546q.jpg)

### Pie Charts with Matplotlib

饼状图还是很好理解的,而且也很常用，如下图所示：

```python
import matplotlib.pyplot as plt
# 24h 分成四个部分
slices = [7,2,2,13]
activities = ['sleeping','eating','working','playing']
# 颜色
cols = ['c','m','r','b']
# 属性的设置
plt.pie(slices,
        labels=activities,
        colors=cols,
        startangle=90,
        shadow= True,
        explode=(0,0.1,0,0),
        autopct='%1.1f%%')

plt.title('Interesting Graph\nCheck it out')
plt.show()
```

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f92b8n7fquj20qf0m57c9.jpg)

本次是课程的第五六节，到此结束。