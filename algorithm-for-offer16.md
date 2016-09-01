### 题目：不用加减乘除做加法



![](http://ww1.sinaimg.cn/large/b10d1ea5jw1f7dtnzvdkkj20kd0c976k.jpg)

``` java
/**
 * Created by allen on 2016/9/1.
 */
public class ex17 {

    public static int add(int x, int y) {
        int sum;
        int carry;
        do {
            // 两位相同为 0 相异为 1
            sum = x ^ y;
            // x&y的某一位是1说明，它是它的前一位的进位，所以向左移动一位
            // & 运算两个位都为 1 时，结果才为 1
            carry = (x & y) << 1;
            x = sum;
            y = carry;
        } while (y != 0);
        return x;
    }
    public static void main(String[] args) {
        System.out.println(add(1, 2) + ", " + (1 + 2));
        System.out.println(add(13, 34)+ ", " + (13 + 34));
        System.out.println(add(19, 85)+ ", " + (19 + 95));
        System.out.println(add(865, 245)+ ", " + (865 + 245));
    }
}

```



