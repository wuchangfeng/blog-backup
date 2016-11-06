### Loading Data from Files for Matplotlib

这一节学习如何从文件中读取数据信息，并绘制图表，在 example.txt 文件中，我们写入如下数据：

``` java
1,5
2,3
3,4
4,7
5,4
6,3
7,5
8,7
9,4
10,4
```

程序代码如下：

``` Python
import matplotlib.pyplot as plt
import csv

x = []
y = []
# 打开文件
with open('example.txt','r') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    for row in plots:
        x.append(int(row[0]))
        y.append(int(row[1]))

plt.plot(x,y, label='Loaded from file!')
plt.xlabel('x')
plt.ylabel('y')
plt.title('Interesting Graph\nCheck it out')
plt.legend()
plt.show()
```

在我们引入了 numpy 这个工具库之后，就可以进行如下简化写法：

``` python
import matplotlib.pyplot as plt
import numpy as np
# 引入 numpy
x, y = np.loadtxt('example.txt', delimiter=',', unpack=True)
plt.plot(x,y, label='Loaded from file!')

plt.xlabel('x')
plt.ylabel('y')
plt.title('Interesting Graph\nCheck it out')
plt.legend()
plt.show()
```

截图如下：

![](http://ww4.sinaimg.cn/large/b10d1ea5jw1f93b8z1q5xj20qf0m5jy0.jpg)