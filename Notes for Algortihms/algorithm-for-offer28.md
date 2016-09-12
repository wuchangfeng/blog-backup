## 题目：求二进制中 1 的个数。

比如说 9 的二进制表示为 1001 则输出 2

### 思路一

如果一旦转换成进制相关，首先想到的就是位操作。这里联想到 与运算。与运算要求两个位置都是 1 的情况下才输出 1. 由于整数 int 是 32 位 8 个字节。而在进行与运算过程中，二进制数要不断的右移来进行与 1 的比较。而 >> 经常要考虑符号位的变化。所以选择 >>>。无符号右移运算符>>> 只是对32位和64位的值有意义。[左移右移操作](http://blog.sina.com.cn/s/blog_99201d890101hd6s.html)

### 思路二

以上思路是基本的，但是考虑到 1 最多要移动 32 位。 另一种是把一个整数 （无论正负或0）减去1，再和原整数做**与运算**，会把该整数最右边的一个1变为0，例如：110100减1后变为110011，二者进行与操作后，得到110000，最后边的1变为了0，而前面的位都不变。 这样，我们可以利用这这一结论来从左向右依次将整数的最右边的1变为0，当该整数的所有位为1的位均变为0之后，便统计到了该整数二进制中1的个数。  



``` java
/**
 * Created by allen on 2016/9/12.
 */
public class ex27 {

    public static int NumofOne(int n){
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result += n & 1;
            n = n >>> 1;
        }
        return  result;
    }

    public static int NumofOne2(int n){
        int result = 0;
        while (n != 0){
            result ++;
            n = n & (n-1);
        }
        return  result;
    }

    public static void main(String[] args) {
        System.out.println(NumofOne2(9));
    }
}

```

