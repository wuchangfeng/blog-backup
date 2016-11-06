### Basic Operations

以下所示为一维数组的一些基本操作：

``` python
>>> a = np.array( [20,30,40,50] )
>>> b = np.arange( 4 )
>>> b
array([0, 1, 2, 3])
>>> c = a-b
>>> c
array([20, 29, 38, 47])
# b 数组乘以2
>>> b**2
array([0, 1, 4, 9])
>>> 10*np.sin(a)
array([ 9.12945251, -9.88031624,  7.4511316 , -2.62374854])
# 输出 < 35
>>> a<35
array([ True, True, False, False], dtype=bool)
```

以下是二维数组的一些基本操作，与其他矩阵语言不同，NumPy 中的**乘法运算符按元素逐个计算**，矩阵乘法可以使用dot函数或创建矩阵对象实现，个人刚开始还是按照数学思维去乘以矩阵，却怎么也计算不出来：

``` python
>>> A = np.array( [[1,1],
...             [0,1]] )
>>> B = np.array( [[2,0],
...             [3,4]] )
# 逐个相乘
>>> A*B                         
array([[2, 0],
       [0, 4]])
# 这个才是数学上的矩阵相乘
>>> A.dot(B)                  
array([[5, 4],
       [3, 4]])
>>> np.dot(A, B)                # another matrix product
array([[5, 4],
       [3, 4]])
```

我们可以在已有的矩阵基础上，进行一些操作，生成新的矩阵，而不用再次创建：

```python
>>> a = np.ones((2,3), dtype=int)
>>> b = np.random.random((2,3))
>>> a *= 3
>>> a
array([[3, 3, 3],
       [3, 3, 3]])
>>> b += a
>>> b
array([[ 3.417022  ,  3.72032449,  3.00011437],
       [ 3.30233257,  3.14675589,  3.09233859]])
# a 与 b 的类型不同，所以不能相加
>>> a += b                  
Traceback (most recent call last):
  ...
TypeError: Cannot cast ufunc add output from dtype('float64') to dtype('int64') with casting rule 'same_kind'
```

通过指定axis参数（即数组的行）对指定的轴(即 X 或者 Y 轴)做相应的运算：

``` python
>>> b = np.arange(12).reshape(3,4)
>>> b
array([[ 0,  1,  2,  3],
       [ 4,  5,  6,  7],
       [ 8,  9, 10, 11]])
>>>
>>> b.sum(axis=0)                            # sum of each column
array([12, 15, 18, 21])
>>>
>>> b.min(axis=1)                            # min of each row
array([0, 4, 8])
>>>
# 当 axis = 3 会报错如下
>>> b.sum(axis=3)
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
  File "/home/allen/.local/lib/python2.7/site-packages/numpy/core/_methods.py", line 32, in _sum
    return umr_sum(a, axis, dtype, out, keepdims)
ValueError: 'axis' entry is out of bounds
>>> 
>>> b.cumsum(axis=1)                         # cumulative sum along each row
array([[ 0,  1,  3,  6],
       [ 4,  9, 15, 22],
       [ 8, 17, 27, 38]])
```

### Indexing, Slicing and Iterating

``` python
# **3 表示 a 的三次方
>>> a = np.arange(10)**3
>>> a
array([  0,   1,   8,  27,  64, 125, 216, 343, 512, 729])
>>> a[2]
8
# 切片操作
>>> a[2:5]
array([ 8, 27, 64])
# 等同于a[0:6:2]= -1000，从开始到第6个位置，每隔一个元素将其赋值为-1000
>>> a[:6:2] = -1000   
>>> a
array([-1000,     1, -1000,    27, -1000,   125,   216,   343,   512,   729])
# 反转 a
>>> a[ : :-1]                                
array([  729,   512,   343,   216,   125, -1000,    27, -1000,     1, -1000])
>>> for i in a:
...     print(i**(1/3.))
...
nan
1.0
nan
3.0
nan
5.0
6.0
7.0
8.0
9.0
```

