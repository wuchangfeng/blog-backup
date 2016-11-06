### Pandas Basics - p.2 Data Analysis with Python and Pandas Tutorial

> In this Data analysis with Python and Pandas tutorial, we're going to clear some of the Pandas basics. Data prior to being loaded into a Pandas Dataframe can take multiple forms, but generally it needs to be a dataset that can form to rows and columns. So maybe a **dictionary** like this:

``` python
web_stats = {'Day':[1,2,3,4,5,6],
             'Visitors':[43,34,65,56,29,76],
             'Bounce Rate':[65,67,78,65,45,52]}
```

接着我们写入代码将字典转化为 DataFrame 结构：

``` python
>>> import pandas as pd
>>> web_stats = {'Day':[1,2,3,4,5,6],
...             'Visitors':[43,34,65,56,29,76],
...             'Bounce Rate':[65,67,78,65,45,52]}
>>> df = pd.DataFrame(web_stats)
```

如下即可，我们可以对这个 dataFrame 结构进行一些操作：

``` python
>>> df.head()
   Bounce Rate  Day  Visitors
0           65    1        43
1           67    2        34
2           78    3        65
3           65    4        56
4           45    5        29
```

``` python
>>> df.tail()
   Bounce Rate  Day  Visitors
1           67    2        34
2           78    3        65
3           65    4        56
4           45    5        29
5           52    6        76
>>> 
```



> The "Day" column fits that bill! Generally, if you have any dated data, the date will be the "index" as this is how all of the data points relate. There are many ways to identify the index, change the index, and so on. We'll cover a couple here. First, on any existing dataframe, we can set a new index like so:
>

``` python
>>> df.set_index('Day', inplace=True)
>>> df
     Bounce Rate  Visitors
Day                       
1             65        43
2             67        34
3             78        65
4             65        56
5             45        29
6             52        76
>>> 
```

类似于上面这样，我们指定了 day 为 index。当然，我们可以提取出特定的列来显示：

``` python
>>> df['Visitors']
Day
1    43
2    34
3    65
4    56
5    29
6    76
Name: Visitors, dtype: int64
>>> 
```

紧接着，我们小小的可视话数据一下，看看 Day 与 Visitors 之间的关系图：

``` python
>>> import matplotlib.pyplot as plt
>>> from matplotlib import style
>>> style.use('fivethirtyeight')
>>> df['Visitors'].plot()
<matplotlib.axes._subplots.AxesSubplot object at 0x7f75fdd6bc90>
>>> plt.show()

```

![](http://ww2.sinaimg.cn/large/b10d1ea5jw1f9e3akx0aqj20hw0hl3zr.jpg)