### 题目：

![](http://ww3.sinaimg.cn/large/b10d1ea5jw1f8tsu6814kj20ly02n0tf.jpg)

题目本身不难，但是要需要考虑的细节有点多，比如指数的问题，是正数、负数、还是 0 ？最后我们处理书的多少次方的时候，可以考虑用递归来帮忙，而这其中我们稍微需要考虑的就是指数是偶数还是奇数。

``` java
public class num9 {

    public static double power(double base, int exponent) {

        // 指数和底数不能同时为0,程序鲁棒性的考虑
        if (base == 0 && exponent == 0) {
            throw new RuntimeException("invalid input. base and exponent both are zero");
        }

        // 指数为0就返回1
        if (exponent == 0) {
            return 1;
        }

        // 求指数的绝对值
        long exp = exponent;
        if (exponent < 0) {
            exp = -exp;
        }
        // 求幂次方
        double result = powerWithUnsignedExponent(base, exp);
     
        // 指数是负数，要进行求倒数
        if (exponent < 0) {
            result = 1 / result;
        }
        // 返回结果
        return result;
    }

    public static double powerWithUnsignedExponent(double base, long exponent) {
        // 如果指数为0，返回1
        if (exponent == 0) {
            return 1;
        }
        // 指数为1，返回底数
        if (exponent == 1) {
            return base;
        }

        // 递归求一半的值.用向右移动来替代除以2的操作
        double result = powerWithUnsignedExponent(base, exponent >> 2);

        // 求最终的值
        result *= result;
        // 如果是奇数就还要剩以一次底数
        if (exponent % 2 != 0) {
            result *= base;
        }
        // 返回结果
        return result;
    }
    public static void main(String[] args) {

        System.out.println(power(2, -4));
        System.out.println(power(2, 4));
        System.out.println(power(2, 0));
    }
}
```

