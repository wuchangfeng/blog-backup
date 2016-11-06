### The Basic

``` python
>>> import numpy as np
# 创建一个 3*5 的 1至14 的举证
>>> a = np.arange(15).reshape(3, 5)
>>> a
array([[ 0,  1,  2,  3,  4],
       [ 5,  6,  7,  8,  9],
       [10, 11, 12, 13, 14]])
# 输出矩阵行和列
>>> a.shape
(3, 5)
# 输出数组的秩，这里有疑问
>>> a.ndim
2
# 数组元素的属性
>>> a.dtype.name
'int64'
# 64/8 得到的
>>> a.itemsize
8
# 数组的元素个数
>>> a.size
15
>>> type(a)
<type 'numpy.ndarray'>
# 创建一维数组
>>> b = np.array([6, 7, 8])
>>> b
array([6, 7, 8])
>>> type(b)
<type 'numpy.ndarray'>
```

关于 a.ndim 这个还不太明白，如果当成秩来求可以理解，但是通常一维数组输出一，二维数组输出二。

### Array Creation

``` python
>>> import numpy as np
# list
>>> a = np.array([2,3,4])
>>> a
array([2, 3, 4])
# tuple
>>> a = np.array((2,3,4))
>>> a
array([2, 3, 4])
>>> a.dtype
dtype('int64')
>>> b = np.array([1.2, 3.5, 5.1])
>>> b.dtype
dtype('float64')
```

Often, the elements of an array are originally unknown, but its size is known. Hence, NumPy offers several functions to create arrays with initial placeholder content。如下几种方式提供了一些创造固定元素数组的功能：

``` python
# 创建三行四列的 0 矩阵
>>> np.zeros( (3,4) )
array([[ 0.,  0.,  0.,  0.],
       [ 0.,  0.,  0.,  0.],
       [ 0.,  0.,  0.,  0.]])
# 指定元素类型
>>> np.ones( (2,3,4), dtype=np.int16 )                # dtype can also be specified
array([[[ 1, 1, 1, 1],
        [ 1, 1, 1, 1],
        [ 1, 1, 1, 1]],
       [[ 1, 1, 1, 1],
        [ 1, 1, 1, 1],
        [ 1, 1, 1, 1]]], dtype=int16)
>>> np.empty( (2,3) )                                 # uninitialized, output may vary
array([[  3.73603959e-262,   6.02658058e-154,   6.55490914e-260],
       [  5.30498948e-313,   3.14673309e-307,   1.00000000e+000]])
```

指定范围内产生指定 step 的数字：

``` python
# 10 到 30 每 5 个数字打印出一次
>>> np.arange( 10, 30, 5 )
array([10, 15, 20, 25])
>>> np.arange( 0, 2, 0.3 )                 # it accepts float arguments
array([ 0. ,  0.3,  0.6,  0.9,  1.2,  1.5,  1.8])
```

如下示例提供了在 0-2 之间产生 9 个随机数的功能，即 linspace 函数

``` python
>>> from numpy import pi
>>> np.linspace( 0, 2, 9 )                 # 9 numbers from 0 to 2
array([ 0.  ,  0.25,  0.5 ,  0.75,  1.  ,  1.25,  1.5 ,  1.75,  2.  ])
>>> x = np.linspace( 0, 2*pi, 100 )        # useful to evaluate function at lots of points
>>> f = np.sin(x)
```

### Print Arrays

``` python
# 按顺序输出 0-5
>>> a = np.arange(6)                       
>>> print(a)
[0 1 2 3 4 5]
>>>
>>> b = np.arange(12).reshape(4,3)           # 2d array
>>> print(b)
[[ 0  1  2]
 [ 3  4  5]
 [ 6  7  8]
 [ 9 10 11]]
>>>
# 按顺序输出三维数组
>>> c = np.arange(24).reshape(2,3,4)       
>>> print(c)
[[[ 0  1  2  3]
  [ 4  5  6  7]
  [ 8  9 10 11]]
 [[12 13 14 15]
  [16 17 18 19]
  [20 21 22 23]]]
# c.ndim 为 3
>>> c.ndim
3
>>> 
```

