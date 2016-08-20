### 题目：求 1+2+3+。。。+n

要求是不能用什么循环啊 if else 啊

用高斯公式呗。

```java
public class ex10 {

    public static int Sum_Solution(int n) {
        // 高斯公式 n*(n+1)/2
        int result = (int) (Math.pow(n, 2) + n);
        // 右移 1 位相当于 1/2
        return result>>1;
    }

    public static void main(String[] args) {


       int sum = Sum_Solution(5);
        System.out.println(sum);

    }
}
```

