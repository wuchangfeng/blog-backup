### Numpy 

Numpy 是一个 Python 科学计算包，提供了强大的多维数组对象，线性代数，丰富的随机数功能。ndarray 就是 Numpy 中表示多维数组的类。

### 将 List 或者 tuple 转换为一维或者二维数组

``` python
# list
>>> print np.array([1,2,3,4])
[1 2 3 4]
# tuple 
>>> print np.array((1.2,2,3,4))
[ 1.2  2.   3.   4. ]
```

转换成二维数组：

``` pyt
>>> print np.array([[1,2,4],[3,4,5]])
[[1 2 4]
 [3 4 5]]
```

### numpy.arange 方法产生随机数

``` python
>>> print np.arange(15)
[ 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14]
# 产生 3 * 5 的矩阵
>>> print np.arange(15).reshape(3,5)
[[ 0  1  2  3  4]
 [ 5  6  7  8  9]
 [10 11 12 13 14]]
```

### numpy.linspace 方法在一定范围内产生随机数

``` python
>>> print np.linspace(1,3,6)
[ 1.    1.25  1.5   1.75  2.    3.  ]
```

### 使用numpy.zeros，numpy.ones，numpy.eye等方法构造特定的矩阵

``` python 
>>> print np.zeros((3,4))
[[ 0.  0.  0.  0.]
 [ 0.  0.  0.  0.]
 [ 0.  0.  0.  0.]]
>>> print np.ones((3,4))
[[ 1.  1.  1.  1.]
 [ 1.  1.  1.  1.]
 [ 1.  1.  1.  1.]]
>>> print np.eye(3)
[[ 1.  0.  0.]
 [ 0.  1.  0.]
 [ 0.  0.  1.]]
```

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f93dbj1qpbj20nj0m5jxg.jpg)

### 基本的矩阵运算

``` python
>>> a = np.array([[1,0],[2,3]])
>>> print a
[[1 0]
 [2 3]]
# 转置
>>> print a.transpose()
[[1 2]
 [0 3]]
# 矩阵的迹
>>> print np.trace(a)
4
```

### 数组索引，切片，赋值

``` python
>>> a = np.array( [[2,3,4],[5,6,7]] )
>>> print a
[[2 3 4]
 [5 6 7]]
# 打印第一行第二列，注意从零开始计数
>>> print a[1,2]
7
# 遍历第一行
>>> print a[1,:]
[5 6 7]
>>> print a[1,1:2]
[6]
>>> a[1,:] = [8,9,10]
>>> print a
[[ 2  3  4]
 [ 8  9 10]]
```

### 使用 for 循环操作元素

``` python
>>> for x in np.linspace(1,3,3):
...     print x
...
1.0
2.0
3.0
```

###  使用vstack 和 hstack 合并数组

![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f93dfmkikij20op0m5dkc.jpg)

