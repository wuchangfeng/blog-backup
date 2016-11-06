### Data Analysis with Python and Pandas Tutorial Introduction

第一节课程： https://pythonprogramming.net/data-analysis-python-pandas-tutorial-introduction/

pandas 数据分析入门第一节课程，作者噼里啪啦扯了很多，大家看看就好，作者引入了一个示例，简单记录一下：

先导入一些包：

``` python
import pandas as pd
import datetime
import pandas.io.data as web
```

在导入第三个包时候，你会发现这个包已经过时了，导入报错。不过没关系， shell 同时推荐你准确的导入包的语句：

``` python
from pandas_datareader import data, wb
```

嗯，这样就好办，我们 `pip install pandas_datareader` 就好办了。接着我们指定时间段来获取数据：

``` python
start = datetime.datetime(2010, 1, 1)
end = datetime.datetime(2015, 8, 22)
```

好了，如下形式，我们就可以获得在 pandas 中基本数据类型之一的 dataFrame 了：

```python
df = data.DataReader("XOM", "yahoo", start, end)
```

用如下语句在终端显示 dataFrame：

``` python
df
```

数据的基本格式如下所示：

``` python
>>> df
                 Open       High        Low      Close    Volume  Adj Close
Date                                                                       
2010-01-04  68.720001  69.260002  68.190002  69.150002  27809100  57.203028
2010-01-05  69.190002  69.449997  68.800003  69.419998  30174700  57.426378
2010-01-06  69.449997  70.599998  69.339996  70.019997  35044700  57.922715
2010-01-07  69.900002  70.059998  69.419998  69.800003  27192100  57.740730
2010-01-08  69.690002  69.750000  69.220001  69.519997  24891800  57.509100
2010-01-11  69.940002  70.519997  69.650002  70.300003  30685000  58.154345
2010-01-12  69.720001  69.989998  69.519997  69.949997  31496700  57.864809
2010-01-13  69.959999  70.040001  69.260002  69.669998  24884400  57.633186
2010-01-14  69.540001  69.739998  69.349998  69.680000  18630800  57.641460
2010-01-15  69.650002  69.690002  68.650002  69.110001  29411900  57.169938

```

当然我们可以根据指定语句 df.head() 获得指定前多少条语句，默认前五条。

恩，作为示例引入教程，作者还是让我们将数据绘图啦，这样看起来更直观，绘图我们要做如下准备工作：

``` python
import matplotlib.pyplot as plt
from matplotlib import style

style.use('fivethirtyeight')
```

显示出来就输入如下语句啦：

``` python
df['High'].plot()# 指定 High 这一列的数据来进行绘图
plt.legend()
plt.show()
```

![](http://ww4.sinaimg.cn/large/b10d1ea5jw1f9e2pcyfgsj20jx0fcmyv.jpg)

如上我们就结束了示例课程第一课。







