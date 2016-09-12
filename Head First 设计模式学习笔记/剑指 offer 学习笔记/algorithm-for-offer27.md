## 题目：斐波那契数列的实现

将递归公式转化为程序循环

![9.png](http://7xrl8j.com1.z0.glb.clouddn.com/9.png?imageMogr2/thumbnail/!75p)

``` java
/**
 * Created by allen on 2016/9/11.
 */
// 斐波那契数列的实现
public class ex26 {

    public static long fibonacci(int n){

        if (n == 0)
            return 0;
        if(n == 1 || n == 2)
            return 1;
        // 当 n = 3 f(n) = f(n-1)+f(n-2) f(1)=1 f(2)=1
        long pre = 1;
        long prePre = 1;
        long currentValue = 0;
        for (int i = 3; i <= n; i++) {
            currentValue = pre + prePre;
            prePre = pre;
            pre = currentValue;
        }
        return currentValue;
    }


    public static void main(String[] args) {
        System.out.println(fibonacci(1));
        System.out.println(fibonacci(2));
        System.out.println(fibonacci(3));
        System.out.println(fibonacci(4));
    }
}

```

